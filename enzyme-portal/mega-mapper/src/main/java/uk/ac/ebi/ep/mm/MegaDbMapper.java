package uk.ac.ebi.ep.mm;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import uk.ac.ebi.ep.search.model.Compound;
import uk.ac.ebi.ep.search.model.Disease;

/**
 * Objects of this class write the mega-map to a database managed via hibernate.
 * @author rafa
 *
 */
public class MegaDbMapper implements MegaMapper {

	private final Logger logger = Logger.getLogger(MegaDbMapper.class);
	protected Session session;
	private final int chunkSize;
	private int counter = 0;
	private SessionFactory sessionFactory;
	
	private Query entryForAccessionQuery,
		xrefsForAccessionQuery, allXrefsForAccessionQuery,
		xrefsForEntryQuery, allXrefsForEntryQuery;
	
	/**
	 * @param dbConfig file name for hibernate configuration. If
	 * 		<code>null</code>, defaults to hibernate default.
	 * @param chunkSize size of the chunks which will be committed to the
	 * 		database to avoid huge sessions and running out of memory.
	 */
	public MegaDbMapper(String dbConfig, int chunkSize){
		sessionFactory = HibernateUtil.getSessionFactory(dbConfig);
		this.chunkSize = chunkSize;
	}
	
	public void openMap() throws IOException {
		openNewSession();
	}

	private void openNewSession() {
		session = sessionFactory.getCurrentSession();
		session.setFlushMode(FlushMode.COMMIT);
		session.beginTransaction();
		entryForAccessionQuery = null;
		xrefsForAccessionQuery = null;
		allXrefsForAccessionQuery =  null;
		xrefsForEntryQuery = null;
		allXrefsForEntryQuery = null;
	}

	/**
	 * Checks if the session has reached the chunk size to commit.
	 */
	private void checkChunkSize() {
		if (counter++ >= chunkSize){
			session.getTransaction().commit();
			openNewSession();
			counter = 0;
		}
	}

	/**
	 * Converts an array of database objects into an array of database names.
	 * @param dbs an array of {@link MmDatabase}
	 * @return an array of String
	 */
	private String[] getDbNames(MmDatabase[] dbs){
		String[] dbNames = new String[dbs.length];
		for (int i = 0; i < dbs.length; i++) {
			dbNames[i] = dbs[i].name();
		}
		return dbNames;
	}
	
	private String getDbNamesAsString(MmDatabase[] dbs){
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < dbs.length; i++) {
			if (i > 0) sb.append(',');
			sb.append('\'').append(dbs[i].name()).append('\'');
		}
		return sb.toString();
	}

	public void writeEntry(Entry entry) throws IOException {
        if (entry.getEntryName() != null && entry.getEntryName().length() > 300){
            logger.warn("[BIG: " + entry.getEntryName().length() + "] "
            		+ entry.getEntryName());
        }
		session.merge(entry); // save or saveOrUpdate does not work!
		logger.debug(entry.getEntryId() + " written");
		checkChunkSize();
	}

	public void writeEntries(Collection<Entry> entries) throws IOException {
		if (entries != null){
			for (Entry entry : entries) {
				writeEntry(entry);
			}
		}
	}

	public void writeXref(XRef xref)
	throws IOException {
		session.merge(xref); // save or saveOrUpdate does not work!
		logger.debug(xref.getFromEntry().getEntryId()
				+ "-" + xref.getToEntry().getEntryId()
				+ " written");
		checkChunkSize();
	}

	public void writeXrefs(Collection<XRef> xrefs) throws IOException {
		if (xrefs != null){
			for (XRef xref : xrefs) {
				writeXref(xref);
			}
		}
	}

	public void write(Collection<Entry> entries,
			Collection<XRef> xrefs) throws IOException {
		writeEntries(entries);
		writeXrefs(xrefs);
	}

	public Entry getEntryForAccession(MmDatabase db, String accession) {
		if (entryForAccessionQuery == null){
			entryForAccessionQuery = session.createQuery("from Entry where" +
					" dbName = :dbName and :accession in elements(entryAccessions)");
		}
		return (Entry) entryForAccessionQuery
				.setString("dbName", db.name())
				.setString("accession", accession)
				.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public Collection<XRef> getXrefs(Entry entry) {
		if (allXrefsForEntryQuery == null){
			allXrefsForEntryQuery = session.createQuery("from XRef" +
					" where fromEntry = :entry or toEntry = :entry");
		}
		return (List<XRef>) allXrefsForEntryQuery
				.setEntity("entry", entry)
				.list();
	}

	@SuppressWarnings("unchecked")
	public Collection<XRef> getXrefs(Entry entry, MmDatabase... dbs) {
		if (xrefsForEntryQuery == null){
			xrefsForEntryQuery = session.createQuery("from XRef" +
					" where (fromEntry = :entry and toEntry.dbName in (:dbNames))" +
					" or (toEntry = :entry and fromEntry.dbName in (:dbNames))");
		}
		return (List<XRef>) xrefsForEntryQuery
				.setEntity("entry", entry)
				.setParameterList("dbNames", getDbNames(dbs))
				.list();
//		String queryString = "from XRef" +
//				" where (fromEntry = :entry and toEntry.dbName in (:dbNames))" +
//				" or (toEntry = :entry and fromEntry.dbName in (" +
//				getDbNamesAsString(dbs)+"))";
//		xrefsForEntryQuery = session.createQuery(queryString);
//		return (List<XRef>) xrefsForEntryQuery
//				.setEntity("entry", entry)
//				.list();
	}

	public Collection<XRef> getXrefs(Collection<Entry> entries,
			MmDatabase... db) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	public Collection<XRef> getXrefs(MmDatabase db, String accession) {
		if (allXrefsForAccessionQuery == null){
			allXrefsForAccessionQuery = session.createQuery("from XRef" +
					" where (fromEntry.dbName = :dbName" +
					" and :accession in elements(fromEntry.entryAccessions))" +
					" or (toEntry.dbName = :dbName" +
					" and :accession in elements(toEntry.entryAccessions))");
		}
		return (List<XRef>) allXrefsForAccessionQuery
				.setString("dbName", db.name())
				.setString("accession", accession)
				.list();
	}

	/**
	 * {@inheritDoc}
	 * <br>
	 * Note that this method is currently slow when the database array has more
	 * than one element. It is probably faster to make separate queries for
	 * each of the databases and then merge the results.
	 * (See {@link MegaDbMapperTest#testGetXrefsByAccession()})
	 */
	@SuppressWarnings("unchecked")
	public Collection<XRef> getXrefs(MmDatabase db, String accession,
			MmDatabase... xDbs) {
		if (xrefsForAccessionQuery == null){
			xrefsForAccessionQuery = session.createQuery("from XRef" +
					" where (fromEntry.dbName = :dbName" +
					" and :accession in elements(fromEntry.entryAccessions)" +
					" and toEntry.dbName in (:xDbNames))" +
					" or (toEntry.dbName = :dbName" +
					" and :accession in elements(toEntry.entryAccessions)" +
					" and fromEntry.dbName in (:xDbNames))");
		}
		return (List<XRef>) xrefsForAccessionQuery
				.setString("dbName", db.name())
				.setString("accession", accession)
				.setParameterList("xDbNames", getDbNames(xDbs))
				.list();
	}

	public void handleError() throws IOException {
		session.getTransaction().rollback();
		logger.error("Session rolled back");
	}

	public void closeMap() throws IOException {
		session.flush();
		session.getTransaction().commit();
		logger.info("Session committed");
//		sessionFactory.close();
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		sessionFactory.close();
	}

	public void commit() {
		session.getTransaction().commit();
	}

	public void rollback() {
		session.getTransaction().rollback();
	}

    public List<String> getAllUniProtAccessions(MmDatabase database) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<Compound> getCompounds(String uniprotId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Entry> getChMBLEntries(MmDatabase db, String accession, MmDatabase... xDb) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getXrefsSize(MmDatabase db, String accession, MmDatabase... xDb) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Map<?, ?> getCompounds(MmDatabase db, String accession, MmDatabase... xDbs) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


	public Collection<XRef> getXrefs(MmDatabase db, String accession,
			Relationship relationship) {
        throw new UnsupportedOperationException("Not supported yet.");
	}

	public Collection<XRef> getXrefs(MmDatabase db, String idFragment,
			Constraint constraint, Relationship relationship) {
        throw new UnsupportedOperationException("Not supported yet.");
	}

	public Collection<XRef> getXrefs(MmDatabase db, String idFragment,
			Constraint constraint, MmDatabase... xDbs) {
        throw new UnsupportedOperationException("Not supported yet.");
	}

    public int updateEntry(Entry entry) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<String> getAllEntryIds(MmDatabase database) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ResultSet getAllEntryIds(MmDatabase database, String query) {
        throw new UnsupportedOperationException("Not supported yet.");
    }



    public Set<uk.ac.ebi.ep.enzyme.model.Disease> getDiseaseByAccession(MmDatabase db, String accessions, MmDatabase... xDbs) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<Disease> getDiseaseByUniprotId(MmDatabase db, String accessions, MmDatabase... xDbs) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    public Entry findByEntryId(String entryId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }



    public Set<Disease> findAllDiseases(MmDatabase uniprotDB, MmDatabase... xDbs) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Set<Disease> findDiseasesLike(MmDatabase uniprotDB, String firstLetter, MmDatabase... xDbs) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<String> getEnzymesByCompound(String compoundId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
 
   public List<String> findEcNumbers() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<CustomXRef> getXrefs_ec_Only(Entry entry, MmDatabase... dbs) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}

