package nl.tudelft.planningstool.api.parameters;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.assertThat;

public class TimeSlotTest {

    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Test
    public void can_create_time_slot() {
        TimeSlot slot = new TimeSlot();

        slot.setStart("2015-08-23");
        slot.setEnd("2015-08-30");

        assertThat(slot.getStart()).isEqualTo(1440280800000L);
        assertThat(slot.getEnd()).isEqualTo(1440885600000L);
    }

    @Test
    public void start_time_must_be_valid() {
        TimeSlot slot = new TimeSlot();

        expected.expect(IllegalArgumentException.class);
        slot.setStart("20-08-30");
    }

    @Test
    public void end_time_must_be_valid() {
        TimeSlot slot = new TimeSlot();

        expected.expect(IllegalArgumentException.class);
        slot.setEnd("2015-30-08");
    }

}
