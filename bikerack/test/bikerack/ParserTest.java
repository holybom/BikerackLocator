package bikerack;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.codeunicorns.bikerack.client.Rack;
import com.codeunicorns.bikerack.server.AdminServiceImpl;
import com.codeunicorns.bikerack.server.RackServiceImpl;


public class ParserTest {

	AdminServiceImpl asi;
	RackServiceImpl rsi;

	@Before
	public void setUp() throws Exception {
		asi = new AdminServiceImpl();
		rsi = new RackServiceImpl();
	}
	
	@Test
	public void testLoadTitle() {
		if (!asi.importData()) fail();
		String[] titleLineRaw = asi.getTitleLine();
		if (titleLineRaw == null) fail();
		if (titleLineRaw.length == 0) fail();
		for (String column : titleLineRaw) {
			if (column == null) fail();
			if (column.compareTo("") == 0) fail();
		}
		if (titleLineRaw[0].compareTo("St number") != 0) fail();
		if (titleLineRaw[1].compareTo("St Name") != 0) fail();	
		if (titleLineRaw[2].compareTo("St Side") != 0) fail();
		if (titleLineRaw[3].compareTo("Skytrain Station Name") != 0) fail();
		if (titleLineRaw[4].compareTo("BIA") != 0) fail();
		if (titleLineRaw[5].compareTo("# of racks") != 0) fail();
		
		testImportData();
	}
	

	public void testImportData() {
		String[] params = {};
		rsi.setBypassPersistence(true);
		asi.loadData(params);
		Rack[] racks = rsi.getRacks();
		if (racks == null) fail(); 
		if (racks.length <= 0) fail();
		for (Rack rack : racks) {
			if (rack == null) fail();
		}
		Rack rack = racks[0];
		testRack(rack);
	}

	// TODO: test with the 2012 first rack
	private void testRack(Rack rack) {
		if (rack.getStreetNum() == 0) fail();
		if (rack.getNumRacks() == 0) fail();
		if (rack.getStreetName() == null) fail();
		if (rack.getStreetSide() == null) fail();
		if (rack.getSkytrain() == null) fail();
		
		if (rack.getStreetNum() != 134) fail();
		if (rack.getStreetName().compareTo("Abbott St") != 0) fail();
		if (rack.getStreetSide().compareTo("East") != 0) fail();
		if (rack.getSkytrain().compareTo("") != 0) fail();
		if (rack.getbIA().compareTo("GT") != 0) fail();	
		if (rack.getNumRacks() != 1 ) fail();
	}
}
