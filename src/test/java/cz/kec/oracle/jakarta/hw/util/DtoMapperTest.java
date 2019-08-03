package cz.kec.oracle.jakarta.hw.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import cz.kec.oracle.jakarta.hw.AbstractTest;
import cz.kec.oracle.jakarta.hw.domain.StreamEntry;
import cz.kec.oracle.jakarta.hw.dto.EntryDto;
import org.junit.jupiter.api.Test;

/**
 * DtoMapperTest
 *
 * @author kec
 * @since 3.8.19
 */
class DtoMapperTest extends AbstractTest {

    @Test
    void convertDtoToDomain() {
        final StreamEntry domObj = DtoMapper.convert(new EntryDto(TEST_AMOUNT_STRING, TEST_TIMESTAMP));
        assertEquals(TEST_AMOUNT, domObj.getAmount());
        assertEquals(TEST_TIMESTAMP, domObj.getTimeStamp());
    }

    @Test
    void convertDomainToDto() {
        final EntryDto dto = DtoMapper.convert(new StreamEntry(null, TEST_AMOUNT, TEST_TIMESTAMP));
        assertEquals(TEST_AMOUNT_STRING, dto.getData().getAmount());
        assertEquals(TEST_TIMESTAMP, dto.getData().getTimeStamp());
    }
}