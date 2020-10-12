package com.example.todomanager.controllers;

import android.content.Context;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class FileController {
    final String STORAGE_FILE = "storage.xml";

    public File getStorageFile(Context context) throws IOException {
        File storage = new File(context.getFilesDir(), STORAGE_FILE);
        storage.createNewFile();
        if (storage.length() == 0) {
            String toWrite = "<tasks></tasks>";
            FileWriter fr = new FileWriter(storage, false);
            fr.write(toWrite);
            fr.close();
        return storage;
    }

    public boolean addTaskToFile(Context context, String content, String xml) throws IOException {
        FileWriter fr = new FileWriter(getStorageFile(context), false);
        String toWrite = XmlController.ROOT_ELEMENT_START + content + xml + XmlController.ROOT_ELEMENT_END;
        fr.write(toWrite);
        fr.close();
        return true;
    }

    public String getStorageContent(Context context) throws ParserConfigurationException, SAXException, IOException {
        String content = "";
        NodeList nodes = getStorageNodesContent(context);
        for (int temp = 0; temp < nodes.getLength(); temp++) {

            Node nNode = nodes.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                Element eElement = (Element) nNode;
                content += XmlController.formTask(eElement.getElementsByTagName("title").item(0).getTextContent(),
                        eElement.getElementsByTagName("description").item(0).getTextContent(),
                        eElement.getElementsByTagName("date").item(0).getTextContent(),
                        eElement.getElementsByTagName("time").item(0).getTextContent());
            }
        }
        return content;
    }

    public String getStorageContent(Context context, String title, String desc, String date, String time) throws ParserConfigurationException, SAXException, IOException {
        String content = "";
        NodeList nodes = getStorageNodesContent(context);
        for (int temp = 0; temp < nodes.getLength(); temp++) {

            Node nNode = nodes.item(temp);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                Element eElement = (Element) nNode;
                if (title.equals(eElement.getElementsByTagName("title").item(0).getTextContent()) &&
                        desc.equals(eElement.getElementsByTagName("description").item(0).getTextContent()) &&
                        date.equals(eElement.getElementsByTagName("date").item(0).getTextContent()) &&
                        time.equals(eElement.getElementsByTagName("time").item(0).getTextContent())) {
                    continue;
                } else {
                    content += XmlController.formTask(eElement.getElementsByTagName("title").item(0).getTextContent(),
                            eElement.getElementsByTagName("description").item(0).getTextContent(),
                            eElement.getElementsByTagName("date").item(0).getTextContent(),
                            eElement.getElementsByTagName("time").item(0).getTextContent());
                }

            }
        }
        return content;
    }

    public List getStorageContentAsList(Context context) throws ParserConfigurationException, SAXException, IOException {

        List<Map<String, String>> data = new ArrayList<>();
        NodeList nodes = getStorageNodesContent(context);
        for (int temp = 0; temp < nodes.getLength(); temp++) {

            Node nNode = nodes.item(temp);

            Log.d("DEBUG_CUSTOM", "\nCurrent Element :" + nNode.getNodeName());

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                Element eElement = (Element) nNode;
                Map<String, String> map = new HashMap<String, String>();
                map.put("title", eElement.getElementsByTagName("title").item(0).getTextContent());
                map.put("description", eElement.getElementsByTagName("description").item(0).getTextContent());
                map.put("date", eElement.getElementsByTagName("date").item(0).getTextContent());
                map.put("time", eElement.getElementsByTagName("time").item(0).getTextContent());
                data.add(map);
            }
        }
        return data;
    }

    public String debugFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public NodeList getStorageNodesContent(Context context) throws IOException, ParserConfigurationException, SAXException {
        File storage = getStorageFile(context);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(storage);

        //optional, but recommended
        //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        doc.getDocumentElement().normalize();

        NodeList nList = doc.getElementsByTagName("task");

        for (int temp = 0; temp < nList.getLength(); temp++) {

            Node nNode = nList.item(temp);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                Element eElement = (Element) nNode;
            }
        }
        return nList;
    }

}

