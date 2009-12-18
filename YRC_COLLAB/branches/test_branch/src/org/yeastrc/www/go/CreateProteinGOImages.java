/*
 * ViewGOTreeAction.java
 * Created on Mar 28, 2005
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.go;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.media.jai.operator.EncodeDescriptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.bio.go.GOGraphGenerator;
import org.yeastrc.bio.go.GONode;
import org.yeastrc.bio.go.GOUtils;
import org.yeastrc.bio.protein.Protein;
import org.yeastrc.db.DBConnectionManager;
import org.yeastrc.nr_seq.NRProtein;
import org.yeastrc.nr_seq.NRProteinFactory;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Mar 28, 2005
 */

public class CreateProteinGOImages extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {
		
		
		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("pdr");
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		
		String sql = "SELECT id FROM YRC_NRSEQ.tblProtein";
		stmt = conn.prepareStatement(sql);
		rs = stmt.executeQuery();
		
		while (rs.next()) {
		
			int id = rs.getInt(1);
			Protein protein = NRProteinFactory.getInstance().getProtein(id);
			
			
			System.out.println ("PARSING: " + protein.getListing() + "...");
			
			Set tmpSet = null;
			Set seedSet = new HashSet();

			tmpSet = (Set)(((NRProtein)(protein)).getGOBiologicalProcess());
			if (tmpSet != null)
				seedSet.addAll( ((NRProtein)(protein)).getGOBiologicalProcess());

			tmpSet = (Set)(((NRProtein)(protein)).getGOCellularComponent());
			if (tmpSet != null)
				seedSet.addAll( ((NRProtein)(protein)).getGOCellularComponent());
			
			tmpSet = (Set)(((NRProtein)(protein)).getGOMolecularFunction());
			if (tmpSet != null)
				seedSet.addAll( ((NRProtein)(protein)).getGOMolecularFunction());
			
			
			if (seedSet.size() < 1)
				continue;
			
			Set goSet = new HashSet();
			
			Iterator iter = seedSet.iterator();
			while (iter.hasNext()) {
				GONode tnode = (GONode)(iter.next());

				goSet.add( tnode );
				goSet.addAll( GOUtils.getAllParents( tnode ) );
			}
			
			GOGraphGenerator gen = new GOGraphGenerator();
			gen.setSeedNodes(seedSet);
			
			BufferedImage bi = gen.getGOGraphImage(goSet);
			
			
			FileOutputStream fos = new FileOutputStream( new File( "C:\\tmp\\images\\" + id + ".jpg" ) );
			EncodeDescriptor.create( (bi), fos, "jpeg", null, null);
			fos.close();
			fos = null;
		}
		
		rs.close(); rs = null;
		stmt.close(); stmt = null;
		
		return null;
	}

}