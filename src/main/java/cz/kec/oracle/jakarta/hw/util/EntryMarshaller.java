package cz.kec.oracle.jakarta.hw.util;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import cz.kec.oracle.jakarta.hw.dto.EntryDto;

/**
 * EntryMarshaller
 *
 * @author kec
 * @since 2.8.19
 */
public class EntryMarshaller {

    private final Unmarshaller jaxbUnmarshaller;

    public EntryMarshaller() {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(EntryDto.Data.class);
            this.jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public EntryDto unmarshall(String xmlString) {
        try {
            EntryDto.Data data = (EntryDto.Data) this.jaxbUnmarshaller.unmarshal(new StringReader(xmlString));
            return new EntryDto(data);
        } catch (JAXBException e) {
            throw new RuntimeException("Error unmarshalling entry data", e);
        }
    }

}
