package cz.kec.oracle.jakarta.hw.dto;

import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.config.PropertyOrderStrategy;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * EntryDto
 *
 * @author kec
 * @since 2.8.19
 */
public class EntryDto {

    public EntryDto() {
        data = new Data();
    }

    public EntryDto(Data data) {
        this.data = data;
    }

    public Data getData() {
        return data;
    }

    public void setData(final Data data) {
        this.data = data;
    }

    private EntryDto.Data data;

    @XmlRootElement(name = "data")
    @JsonbPropertyOrder(PropertyOrderStrategy.REVERSE)
    public static class Data {

        private Float amount;
        private Long timeStamp;

        public Data() {
        }

        public Float getAmount() {
            return amount;
        }

        public void setAmount(final Float amount) {
            this.amount = amount;
        }

        public Long getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(final Long timeStamp) {
            this.timeStamp = timeStamp;
        }
    }
}
