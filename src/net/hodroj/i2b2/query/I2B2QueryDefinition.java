package net.hodroj.i2b2.query;

import java.io.ByteArrayInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class I2B2QueryDefinition {

	private String name;
	private ArrayList<I2B2QueryPanel> panels;
	
	public I2B2QueryDefinition(String xmlDef) {
		Document doc;
		try {
			doc = fromXml(xmlDef);
			
			// parse query name
			this.name = getNodeText(doc, "query_name");
			
			// load panels
			NodeList nodeList = doc.getElementsByTagName("panel");
			panels = new ArrayList<I2B2QueryPanel>();
			
			loadPanels(nodeList);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList<I2B2QueryPanel> getPanels() {
		return panels;
	}
	
	public ArrayList<I2B2QueryPanel> getExcludedPanels() {
		return null;
	}
	
	
	
	
	// Few helper methods
	private void loadPanels(NodeList nodeList) throws Exception {
		for(int i = 0; i < nodeList.getLength(); i++) {
			I2B2QueryPanel panel = createPanel(nodeList.item(i));
			panels.add(panel);
		}
	}
	
	
	private static I2B2QueryPanel createPanel(Node panelNode) throws ParseException {
		int panelNumber = 0;
		boolean excluded = false;
		ArrayList<I2B2QueryItem> items = new ArrayList<I2B2QueryItem>();
		Date startDate = null;
		Date stopDate = null;
		
		// Load from XML
		panelNumber = Integer.parseInt(getNodeText(panelNode, "panel_number"));
		
		// get exclusion criteria
		String invert = getNodeText(panelNode, "invert");
		if(invert.equals("1")) {
			excluded = true;
		}
		
		
		String dateFrom = getNodeText(panelNode, "panel_date_from");
		String dateTo = getNodeText(panelNode, "panel_date_to");
		
		
		// Get 
		if(dateFrom != null) {
			startDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateFrom.replace("Z", ""));
		}
		
		if(dateTo != null) {
			stopDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateTo.replace("Z", ""));
		}
		
		// Add occurs constraint if it exists
		String occursString = getNodeText(panelNode, "total_item_occurrences");
		int occurrences = Integer.parseInt(occursString);
		
		// Add items
		for(int i = 0; i < panelNode.getChildNodes().getLength(); i++) {
			Node child = panelNode.getChildNodes().item(i);
			if(child.getNodeType() == Node.ELEMENT_NODE) {
		    	if(child.getNodeName().equals("item")) {
		    		I2B2QueryItem item = createPanelItem(child);
		    		items.add(item);
		    	}
		    }
		}
		
		// create query panel object
		return new I2B2QueryPanel(panelNumber, occurrences, excluded, startDate, stopDate, items);
	}
	
	private static I2B2QueryItem createPanelItem(Node itemNode) {
		int level = 0;
		String name = null;
		String key = null;
		String domain = null;
		
		
		// Load from XML
		name = getNodeText(itemNode, "item_name");
		level = Integer.parseInt(getNodeText(itemNode, "hlevel"));
		String itemKey = getNodeText(itemNode, "item_key");
		int index = itemKey.indexOf('\\', 2);
		key = itemKey.substring(index);
		
		
		// load value constraint
		I2B2QueryItemValueConstraint valueConstraint = null;
		String constrainByValue = getNodeText(itemNode, "constrain_by_value");
		if(constrainByValue != null) {
			// Create numeric constraint
			valueConstraint = createNumericConstraint(itemNode);
		}
		
		return new I2B2QueryItem(level, name, key, valueConstraint);
	}
	
	private static I2B2QueryItemValueConstraint createNumericConstraint(Node itemNode) {
		float left = 0;
		float right = 0;
		ItemValueOperatorType operator = ItemValueOperatorType.GT;
		
		// Load from XML
		Node constraintByValueNode = null;
		for(int i = 0; i < itemNode.getChildNodes().getLength(); i++) {
			constraintByValueNode = itemNode.getChildNodes().item(i);
			if(constraintByValueNode.getNodeType() == Node.ELEMENT_NODE) {
		    	if(constraintByValueNode.getNodeName().equals("constrain_by_value")) {
		    		break;
		    	}
		    }
		}
		String op = getNodeText(constraintByValueNode, "value_operator");
		String constraint = getNodeText(constraintByValueNode, "value_constraint");
		
		operator = ItemValueOperatorType.valueOf(op);
		if(operator.equals(ItemValueOperatorType.BETWEEN)) {
			String[] operands = constraint.split(" and ");
			left = Float.parseFloat(operands[0]);
			right = Float.parseFloat(operands[1]);
		} else {
			left = Float.parseFloat(constraint);
		}
		
		return new I2B2QueryItemValueConstraint(left, right, operator);
	}
	
	private static String getNodeText(Document document, String nodeTagName) {
		NodeList nodeList = document.getElementsByTagName(nodeTagName);
	    Node node = nodeList.item(0);
	    if(node.getNodeType() == Node.ELEMENT_NODE) {
	    	Element element = (Element)node;
	    	return element.getTextContent();
	    }
	    
	    return null;
	}
	
	public static String getNodeText(Node node, String name) {
		NodeList list = node.getChildNodes();
		
		for(int i = 0; i < list.getLength(); i++) {
			Node child = list.item(i);
			if(child.getNodeType() == Node.ELEMENT_NODE) {
		    	if(name.equals(child.getNodeName())) {
		    		Element element = (Element)child;
		    		return element.getTextContent();
		    	}
		    }
		}
		return null;
	}
	
	public static Node getNode(Node node, String name) {
		NodeList list = node.getChildNodes();
		
		for(int i = 0; i < list.getLength(); i++) {
			Node child = list.item(i);
			if(child.getNodeType() == Node.ELEMENT_NODE) {
		    	if(name.equals(child.getNodeName())) {
		    		return child;
		    	}
		    }
		}
		return null;
	}
	
	public static String getAttributeText(Node node, String name, String attributeName) {
		NodeList list = node.getChildNodes();
		
		
		for(int i = 0; i < list.getLength(); i++) {
			Node child = list.item(i);
			if(child.getNodeType() == Node.ELEMENT_NODE) {
		    	if(name.equals(child.getNodeName())) {
		    		Element element = (Element)child;
		    		return element.getAttribute(attributeName);
		    	}
		    }
		}
		return null;
	}
	
	private static Document fromXml(String xml)  throws Exception {
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();

		return builder.parse(new ByteArrayInputStream(xml.getBytes()));
		 
	}
}
