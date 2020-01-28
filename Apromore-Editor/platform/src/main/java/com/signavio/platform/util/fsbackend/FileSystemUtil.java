/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package com.signavio.platform.util.fsbackend;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class FileSystemUtil {
	
	public static String getCleanFileName(String rawName) {
		return rawName.replaceAll("/|\\\\|:|\\*|\\?|\\\"|<|>|\\||;|$|%|&", "");
	}
	
	public static boolean isFileExistent(String path) {
		synchronized (path.intern()) {
			File file = new File(path);
			return file.exists();
		}
	}
	
	public static boolean isFileAccessible(String path) {
		synchronized (path.intern()) {
			File file = new File(path);
			return file.canRead() && file.canWrite();
		}
	}
	
	public static boolean isFileDirectory(String path) {
		synchronized (path.intern()) {
			File file = new File(path);
			return file.isDirectory();
		}
	}
	
	public static File createFile(String path) {
		synchronized (path.intern()) {
			File f = new File(path);
			try {
				if (f.exists()){
					return null;
				} if (f.createNewFile()) {
					return f;
				} else {
					return null;
				}
			} catch (IOException e) {
				return null;
			}
		}
	}
	
	public static File createDirectory(String path){
		synchronized (path.intern()) {
			File f = new File(path);
			if (f.mkdir()) {
				return f;
			} else {
				return null;
			}	
		}
	}
	
	public static boolean renameFile(String path, String newPath) {
		synchronized (path.intern()) {
			File newFile = new File (newPath);
			if (newFile.exists()) {
				return false;
			}
			File oldFile = new File(path);
			return oldFile.renameTo(newFile);
		}
	}

	public static File[] getFileChildren(String path, FilenameFilter filter) {
		synchronized (path.intern()) {
			File file = new File(path);
			if (filter == null) {
				return file.listFiles();
			} else {
				return file.listFiles(filter);
			}
		}
	}
	
	public static void deleteFileOrDirectory(String path) {
		synchronized (path.intern()) {
			File f = new File (path) ;
			deleteFileOrDirectory(f);
		}
	}
	
	private static void deleteFileOrDirectory(File f) {
		if (f.isDirectory()) {
			for (File child : f.listFiles()) {
				deleteFileOrDirectory(child);
			}
		}
		f.delete();
	}

	public static class WriteOperation {
		public String nodeName, attributeName, stringValue;
		public boolean asCData;
		public WriteOperation(String nodeName, String attributeName, String stringValue, boolean asCData) {
			this.nodeName = nodeName;
			this.attributeName = attributeName;
			this.stringValue = stringValue;
			this.asCData = asCData;
		}
	}
	
	public static File createFile(String path, String xmlString) {
		synchronized (path.intern()) {
			File f = createFile(path);
			if (f != null) {
				try {
					FileWriter writer = new FileWriter(f);
					writer.write(xmlString);
					writer.close();
				} catch (IOException e) {
					return null;
				}
			}
			return f;
		}
	}
	
	public static String readXmlNodeChildFromFile(String xPath, String path, NamespaceContext nsContext) {
		synchronized (path.intern()) {
			File f = new File(path);
			DocumentBuilderFactory xmlFact = DocumentBuilderFactory.newInstance();
			xmlFact.setNamespaceAware(nsContext != null);
			try {
				DocumentBuilder builder = xmlFact.newDocumentBuilder();
				Document doc = builder.parse(f);
				XPath xpath = XPathFactory.newInstance().newXPath();
				if (nsContext != null) {
					xpath.setNamespaceContext(nsContext);
				}
				String s = xpath.evaluate(xPath, doc);
				return s.trim();
			} catch (XPathExpressionException e) {
				return "";
			} catch (FileNotFoundException e) {
				return "";
			} catch (ParserConfigurationException e) {
				return "";
			} catch (SAXException e) {
				return "";
			} catch (IOException e) {
				return "";
			}
		}
	}
	
	public static boolean writeXmlNodeChildToFile(String nodeName, String stringValue, boolean asCData, String fileName) {
		return writeXmlNodeChildToFile(new WriteOperation[] { new WriteOperation(nodeName, null, stringValue, asCData )} , fileName );
	}
	
	public static boolean writeXmlNodeAttributeToFile(String nodeName, String attributeName, String stringValue, String fileName) {
		return writeXmlNodeChildToFile(new WriteOperation[] { new WriteOperation(nodeName, attributeName, stringValue, false )} , fileName );
	}
	
	private static boolean writeXmlNodeChildToFile(WriteOperation[] operations, String path) {
		synchronized (path.intern()) {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			docFactory.setNamespaceAware(true);
			try {
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
				File file = new File(path);
				Document doc = docBuilder.parse(file);
				for (WriteOperation operation : operations) {
					String nodeName = operation.nodeName;
					boolean handleAttribute = operation.attributeName != null;
					
					if (nodeName.contains(":")) {
						nodeName = nodeName.substring(nodeName.lastIndexOf(':')+1);
					}
					NodeList nodelist = doc.getElementsByTagName(nodeName);
					if (nodelist.getLength() != 1) {
						return false;
					}
					Node node = nodelist.item(0);
					
					
					if (handleAttribute) {
						NamedNodeMap attributes = node.getAttributes();
						Attr newAttr = doc.createAttribute(operation.attributeName);
						newAttr.setNodeValue(operation.stringValue);
						attributes.setNamedItem(newAttr);
					} else {
						NodeList children = node.getChildNodes();
						for (int j = children.getLength() - 1; j >= 0; j--) {
							node.removeChild(children.item(j));
						}
						if (operation.asCData) {
							if (node.appendChild(doc.createCDATASection(operation.stringValue)) == null) {
								return false;
							}
						} else {
							if (node.appendChild(doc.createTextNode(operation.stringValue)) == null) {
								return false;
							}
						}
					}
				}
				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	
				StreamResult result = new StreamResult(new FileWriter(new File(path)));
				
				File f = new File(path);
				if (!f.exists() || !f.canRead() || !f.canWrite()) {
					return false;
				}
				
				DOMSource source = new DOMSource(doc);
				transformer.transform(source, result);
				
				result.getWriter().close();
				
				return true;
	
			} catch (FileNotFoundException e) {
				return false;
			} catch (IOException e) {
				return false;
			} catch (SAXException e) {
				return false;
			} catch (TransformerConfigurationException e) {
				return false;
			} catch (TransformerFactoryConfigurationError e) {
				return false;
			} catch (TransformerException e) {
				return false;
			} catch (ParserConfigurationException e) {
				return false;
			}
		}
	}

}
