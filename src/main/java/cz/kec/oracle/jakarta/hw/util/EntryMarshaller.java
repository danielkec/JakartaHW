package cz.kec.oracle.jakarta.hw.util;

import java.io.StringReader;
import java.io.StringWriter;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import cz.kec.oracle.jakarta.hw.dto.EntryDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EntryMarshaller
 *
 * @author kec
 * @since 2.8.19
 */
public class EntryMarshaller {

    private Logger LOG = LoggerFactory.getLogger(EntryMarshaller.class);

    private final Unmarshaller jaxbUnmarshaller;
    private final Jsonb jsonb;
    private final Marshaller jaxbMarshaller;

    public EntryMarshaller() {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(EntryDto.Data.class, EntryDto.class);
            this.jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            this.jaxbMarshaller = jaxbContext.createMarshaller();

        } catch (JAXBException e) {
            throw new LoggingRuntimeException("Error when preparing marshallers", e);
        }
        this.jsonb = JsonbBuilder.create();
    }

    public EntryDto unmarshallXml(String xmlString) {
        try {
            EntryDto.Data data = (EntryDto.Data) this.jaxbUnmarshaller.unmarshal(new StringReader(xmlString));
            return new EntryDto(data);
        } catch (JAXBException e) {
            throw new LoggingRuntimeException("Error unmarshalling entry data", e);
        }
    }

    public String marshallToJson(EntryDto entryDto) {
        return jsonb.toJson(entryDto);
    }

    public String marshallToXml(EntryDto entryDto) {
        StringWriter stringWriter = new StringWriter();
        try {
            jaxbMarshaller.marshal(entryDto.getData(), stringWriter);
            return stringWriter.toString();
        } catch (JAXBException e) {
            throw new LoggingRuntimeException("Error marshalling entry data", e);
        }
    }

}
