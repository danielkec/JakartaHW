package cz.kec.oracle.jakarta.hw.util;

import cz.kec.oracle.jakarta.hw.domain.StreamEntry;
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
        entryDto.getData().setAmount(streamEntry.getAmount().toString());
        entryDto.getData().setTimeStamp(streamEntry.getTimeStamp());
        return entryDto;
    }

    public static StreamEntry convert(EntryDto entryDto) {
        return new StreamEntry(null, Float.parseFloat(entryDto.getData().getAmount()), entryDto.getData().getTimeStamp(), null);
    }
}
