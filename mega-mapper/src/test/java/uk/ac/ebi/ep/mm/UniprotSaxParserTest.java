package uk.ac.ebi.ep.mm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class UniprotSaxParserTest {

	UniprotSaxParser parser;
	
	@Before
	public void init(){
		parser = new UniprotSaxParser();
	}

	@Test
	public void testCharacters() throws Exception {
		// isEntry, isAccession, isEntryName... flags are all false
		parser.characters("foo".toCharArray(), 0, 3);
		// we don't expect nothing to be recorded:
		assertEquals(0, parser.currentChars.length());
		assertEquals(0, parser.accessions.size());
		assertEquals(0, parser.entryNames.size());
		assertEquals(0, parser.ecs.size());
		assertNull(parser.orgSciName);
		parser.isEntry = true;
		parser.characters("foo".toCharArray(), 0, 3);
		// nothing yet, only recording for interesting elements:
		assertEquals(0, parser.currentChars.length());
		assertEquals(0, parser.accessions.size());
		assertEquals(0, parser.entryNames.size());
		assertEquals(0, parser.ecs.size());
		assertNull(parser.orgSciName);
		parser.isAccession = true;
		parser.characters("foo".toCharArray(), 0, 3);
		// Now we should get something:
		assertEquals("foo", parser.currentChars.toString());
		parser.characters("bar".toCharArray(), 0, 3);
		assertEquals("foobar", parser.currentChars.toString());
		parser.characters("baz".toCharArray(), 0, 3);
		assertEquals("foobarbaz", parser.currentChars.toString());
	}

	@Test
	public void testStartElement() throws Exception {
		parser.startElement(null, "uniprot", null, null);
		assertFalse(parser.isEntry);
		assertFalse(parser.isAccession);
		assertFalse(parser.isEntryName);
		assertFalse(parser.isDbRef);
		assertFalse(parser.isOrgSciName);
		parser.startElement(null, "entry", null, null);
		assertTrue(parser.isEntry);
		assertFalse(parser.isAccession);
		assertFalse(parser.isEntryName);
		assertFalse(parser.isDbRef);
		assertFalse(parser.isOrgSciName);
		parser.startElement(null, "accession", null, null);
		assertFalse(parser.isEntry);
		assertTrue(parser.isAccession);
		assertFalse(parser.isEntryName);
		assertFalse(parser.isDbRef);
		assertFalse(parser.isOrgSciName);
		parser.startElement(null, "foo", null, null);
		assertFalse(parser.isEntry);
		assertFalse(parser.isAccession);
		assertFalse(parser.isEntryName);
		assertFalse(parser.isDbRef);
		assertFalse(parser.isOrgSciName);
	}

	@Test
	public void testEndElement() throws Exception {
		parser.startElement(null, "uniprot", null, null);
		assertEquals(0, parser.accessions.size());
		parser.startElement(null, "entry", null, null);
		assertEquals(0, parser.accessions.size());
		parser.startElement(null, "accession", null, null);
		assertEquals(0, parser.accessions.size());
		parser.characters("foo".toCharArray(), 0, 3);
		assertEquals(0, parser.accessions.size());
		parser.endElement(null, "accession", null);
		assertEquals(1, parser.accessions.size());
		parser.startElement(null, "accession", null, null);
		assertEquals(1, parser.accessions.size());
		parser.characters("bar".toCharArray(), 0, 3);
		assertEquals(1, parser.accessions.size());
		parser.endElement(null, "accession", null);
		assertEquals(2, parser.accessions.size());
	}

}
