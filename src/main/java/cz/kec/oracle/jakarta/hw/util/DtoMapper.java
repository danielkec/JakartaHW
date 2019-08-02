package cz.kec.oracle.jakarta.hw.util;

import cz.kec.oracle.jakarta.hw.StreamEntry;
import cz.kec.oracle.jakarta.hw.dto.EntryDto;

/**
 * DtoMapper
 *
 * @author kec
 * @since 3.8.19
 */
public class DtoMapper {

    public static EntryDto convert(StreamEntry streamEntry) {
        EntryDto entryDto = new EntryDto();
        entryDto.getData().setAmount(streamEntry.getAmount());
        entryDto.getData().setTimeStamp(streamEntry.getTimeStamp());
        return entryDto;
    }
}
