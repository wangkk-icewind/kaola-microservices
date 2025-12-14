package com.kaola.common.util;

import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * XML工具类
 *
 * @author Kaola Team
 */
@Slf4j
public class XmlUtil {

    /**
     * Map转XML字符串
     */
    public static String mapToXml(Map<String, String> data) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();

            Element root = document.createElement("xml");
            document.appendChild(root);

            for (Map.Entry<String, String> entry : data.entrySet()) {
                Element element = document.createElement(entry.getKey());
                element.appendChild(document.createCDATASection(entry.getValue()));
                root.appendChild(element);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(writer));

            return writer.toString();
        } catch (Exception e) {
            log.error("Map转XML失败", e);
            return null;
        }
    }

    /**
     * XML字符串转Map
     */
    public static Map<String, String> xmlToMap(String xml) {
        try {
            Map<String, String> data = new HashMap<>();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // 禁用外部实体解析，防止XXE攻击
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

            Element root = document.getDocumentElement();
            NodeList nodeList = root.getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    data.put(element.getNodeName(), element.getTextContent());
                }
            }

            return data;
        } catch (Exception e) {
            log.error("XML转Map失败", e);
            return null;
        }
    }

    /**
     * 生成简单XML字符串（不使用CDATA）
     */
    public static String mapToSimpleXml(Map<String, String> data) {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");

        for (Map.Entry<String, String> entry : data.entrySet()) {
            sb.append("<").append(entry.getKey()).append(">");
            sb.append(entry.getValue());
            sb.append("</").append(entry.getKey()).append(">");
        }

        sb.append("</xml>");
        return sb.toString();
    }

    /**
     * 从XML中获取指定节点的值
     */
    public static String getValueFromXml(String xml, String tagName) {
        Map<String, String> data = xmlToMap(xml);
        return data != null ? data.get(tagName) : null;
    }
}
