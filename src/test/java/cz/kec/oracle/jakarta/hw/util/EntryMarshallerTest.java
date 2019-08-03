package cz.kec.oracle.jakarta.hw.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import cz.kec.oracle.jakarta.hw.AbstractTest;
import cz.kec.oracle.jakarta.hw.dto.EntryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * EntryMarshallerTest
 *
 * @author kec
 * @since 3.8.19
 */
class EntryMarshallerTest extends AbstractTest {

    private static Pattern AMOUNT_JSON_PATTERN = Pattern.compile("amount\\\"\\s*:\\s*\\\"([^\"]+)\\\"");
    private static Pattern TIMESTAMP_JSON_PATTERN = Pattern.compile("timeStamp\\\"\\s*:\\s*(\\d+)");

    private EntryMarshaller marshaller;

    @BeforeEach
    void setUp() {
        this.marshaller = new EntryMarshaller();
    }

    @Test
    void unmarshallXml() {
        String xml = String.format("<data><timeStamp>%s</timeStamp><amount>%s</amount></data>", TEST_TIMESTAMP, TEST_AMOUNT_STRING);
        final EntryDto dto = marshaller.unmarshallXml(xml);
        assertEquals(TEST_TIMESTAMP, dto.getData().getTimeStamp());
        assertEquals(TEST_AMOUNT_STRING, dto.getData().getAmount());
    }

    @Test
    void marshallToJson() {
        EntryDto dto = new EntryDto(TEST_AMOUNT_STRING, TEST_TIMESTAMP);
        final String json = marshaller.marshallToJson(dto);
        final Matcher amountMatcher = AMOUNT_JSON_PATTERN.matcher(json);
        final Matcher timeStampMatcher = TIMESTAMP_JSON_PATTERN.matcher(json);
        assertTrue(amountMatcher.find(), json + " doesn't match regex " + AMOUNT_JSON_PATTERN.pattern());
        assertEquals(TEST_AMOUNT_STRING, amountMatcher.group(1));
        assertTrue(timeStampMatcher.find(), json + " doesn't match regex " + TIMESTAMP_JSON_PATTERN.pattern());
        assertEquals(String.valueOf(TEST_TIMESTAMP), timeStampMatcher.group(1));
    }

    @Test
    void marshallToXml() throws ParserConfigurationException, IOException, SAXException {
        EntryDto dto = new EntryDto(TEST_AMOUNT_STRING, TEST_TIMESTAMP);
        final String xml = marshaller.marshallToXml(dto);
        final Document dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
        final NodeList amountNodeList = dom.getElementsByTagName("amount");
        final NodeList timeStampNodeList = dom.getElementsByTagName("timeStamp");
        assertEquals(1, amountNodeList.getLength());
        assertEquals(TEST_AMOUNT_STRING, amountNodeList.item(0).getTextContent());
        assertEquals(1, timeStampNodeList.getLength());
        assertEquals(String.valueOf(TEST_TIMESTAMP), timeStampNodeList.item(0).getTextContent());
    }
}