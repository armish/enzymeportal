package uk.ac.ebi.ep.mm;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RelationshipTest {

	@Test
	public void testBetween() {
		assertEquals(Relationship.belongs_to,
				Relationship.between(MmDatabase.UniProt, MmDatabase.Linnean));
		assertEquals(Relationship.belongs_to,
				Relationship.between(MmDatabase.UniProt, MmDatabase.EC));
		assertEquals(Relationship.is_part_of,
				Relationship.between(MmDatabase.UniProt, MmDatabase.PDB));
		assertEquals(Relationship.is_related_to,
				Relationship.between(MmDatabase.UniProt, MmDatabase.ChEBI));
	}

}
