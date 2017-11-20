/**
 * Created on Dec 3, 2015
 * @author cskim -- hufs.ac.kr, Dept of CSE
 * Copy Right -- Free for Educational Purpose
 */

package hufs.ces.svggrim;

import java.util.HashMap;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author cskim
 *
 */
public class SaxSVGParseHandler extends DefaultHandler{

	private GrimPanModel model = null;

	public SaxSVGParseHandler(GrimPanModel model){
		this.model = model;
	}
	@Override
	public void startElement(String uri,String qName,String lName,Attributes atts){
		//System.out.println("start lName="+lName);
		if (lName.equals("path")){
			//String pathDef = atts.getValue("d");			
			//System.out.println("d="+pathDef);
			
			HashMap<String, String> attsMap = new HashMap<String, String>();
			for (int i=0;i<atts.getLength();i++){
				String attname = atts.getLocalName(i); // use xmlparsev2.jar
				String attvalue = atts.getValue(attname);
				attsMap.put(attname, attvalue);
				//System.out.println(attname+"="+attvalue);
			}
			model.attsMapList.add(attsMap);
						
		}
	}

}
