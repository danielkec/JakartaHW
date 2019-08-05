package cz.kec.oracle.jakarta.hw;

import java.util.PriorityQueue;

import cz.kec.oracle.jakarta.hw.domain.StreamEntry;

/**
 * MergingMinHeap
 *
 * @author kec
 * @since 5.8.19
 */
public class MergingMinHeap extends PriorityQueue<StreamEntry> {

    public StreamEntry pollAndMerge() {
        StreamEntry lastPeeked = peek();
        if (lastPeeked == null) {
            return null;
        }
        poll();
        while (peek() != null && peek().getTimeStamp().equals(lastPeeked.getTimeStamp())) {
            lastPeeked = lastPeeked.mergeAmount(peek());
            poll();
        }
        return lastPeeked;
    }

}
