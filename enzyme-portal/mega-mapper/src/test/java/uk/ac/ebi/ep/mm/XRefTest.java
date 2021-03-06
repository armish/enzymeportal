package uk.ac.ebi.ep.mm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/*
 * The test is slow mainly because of the setup and cleanup procedures.
 */
@Ignore("hibernate is deprecated in this module")
public class XRefTest {

	private SessionFactory sessionFactory;
	private List<Integer> entityIds = new ArrayList<Integer>();
	private List<String> entryIds = new ArrayList<String>();
	private Logger logger = Logger.getLogger("JUNIT");
	
	@Before
	public void before(){
        logger.info("Before setting up");
		sessionFactory = HibernateUtil.getSessionFactory();
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		
		Entry entity1 = new Entry();
		entity1.setDbName(MmDatabase.UniProt.name());
		entity1.setEntryId("ABCD_VOGON");
		entity1.setEntryAccessions(Collections.singletonList("V12345"));
		entityIds.add((Integer) session.save(entity1));
		entryIds.add(entity1.getEntryId());
		
		Entry entity2 = new Entry();
		entity2.setDbName(MmDatabase.ChEBI.name());
		entity2.setEntryId("CHEBI:XXXXX");
		entity2.setEntryName("vogonic acid");
		entityIds.add((Integer) session.save(entity2));
		entryIds.add(entity2.getEntryId());
		
		session.getTransaction().commit();
        logger.info("After setting up");
	}
	
	@After
	public void after(){
        logger.info("Before cleanig up");
		Session session = sessionFactory.getCurrentSession();
		Transaction tr = session.beginTransaction();
		String relQuery = "FROM XRef WHERE fromEntry = :fromEnt OR toEntry = :toEnt";
		for (Integer id : entityIds) {
			@SuppressWarnings("unchecked")
			List<XRef> xrefs = session.createQuery(relQuery)
					.setInteger("fromEnt", id)
					.setInteger("toEnt", id)
					.list();
			for (XRef xRef : xrefs) {
				session.delete(xRef);
				System.out.println("XRef removed");
			}
		}
		String entQuery = "DELETE FROM Entry" +
				" WHERE entryId LIKE '%_VOGON' OR entryName LIKE 'vogo%'";
		int m = session.createQuery(entQuery).executeUpdate();
		System.out.println(m + " entries deleted");
		tr.commit();
    	sessionFactory.close();
        logger.info("After cleanig up");
	}
	
	@Test
	public void test() {
        try {
            logger.info("Before getting session");
    		Session session = sessionFactory.getCurrentSession();
            logger.info("Before beginning transaction");
    		session.beginTransaction();
            logger.info("Before getting entries");
    		Entry ent1 = (Entry) session.get(Entry.class, entityIds.get(0));
    		Entry ent2 = (Entry) session.get(Entry.class, entityIds.get(1));
    		XRef xref = new XRef();
    		xref.setFromEntry(ent2);
    		xref.setToEntry(ent1);
    		xref.setRelationship(Relationship.is_drug_for.name());
            logger.info("Before saving xref");
    		session.save(xref);
            logger.info("Before committing");
    		session.getTransaction().commit();
    		
            logger.info("Before getting session");
    		session = sessionFactory.getCurrentSession();
            logger.info("Before beginning transaction");
    		session.beginTransaction();
            logger.info("Before creating queries");
    		final Query fromQuery =
    				session.createQuery("FROM XRef WHERE fromEntry = :fromEnt");
    		final Query toQuery =
    				session.createQuery("FROM XRef WHERE toEntry = :toEnt");
            logger.info("Before running queries");
            // ~1s to run these four queries (2012-01-06)
			assertEquals(0, fromQuery.setEntity("fromEnt", ent1).list().size());
			assertEquals(1, fromQuery.setEntity("fromEnt", ent2).list().size());
			assertEquals(1, toQuery.setEntity("toEnt", ent1).list().size());
			assertEquals(0, toQuery.setEntity("toEnt", ent2).list().size());
            logger.info("After running queries");
        } catch (Exception e){
        	fail();
        }
	}

}
