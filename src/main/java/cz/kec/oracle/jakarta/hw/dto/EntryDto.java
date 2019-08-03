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
@XmlRootElement
public class EntryDto {

    public EntryDto() {
        data = new Data();
    }

    public EntryDto(String amount, Long timeStamp) {
        data = new Data();
        data.setAmount(amount);
        data.setTimeStamp(timeStamp);
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

        private String amount;
        private Long timeStamp;

        public Data() {
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(final String amount) {
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
