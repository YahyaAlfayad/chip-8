package cpu;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by kinder112 on 03.07.2016.
 */
class Chip8TestBase {
    Chip8 cpu;

    @Before
    public void setUp() throws Exception {
        cpu = new Chip8();
    }

    void assertThatProgramCounterIs(int previousFrameAddress) {
        //Chip8 PC is 12 bit but in Java I've used short to represent it that's why 0xFFF
        assertThat(cpu.programCounter, is((short) (previousFrameAddress & 0xFFF)));
    }

    void assertThatStackPointerIs(int expectedStackPointer) {
        assertThat(cpu.stackPointer, is((byte) (expectedStackPointer & 0xFF)));
    }

    void assertThatTopStackIs(int previousPC) {
        assertThat(cpu.stack[cpu.stackPointer - 1], is(((short) (previousPC & 0xFFFF))));
    }

    private String convertByteToStarsAndSpaces(byte b) {
        //Only high nibble (4 bytes) are used to represent font
        return Integer.toBinaryString((b >> 4) & 0xF | 0x100)
                .substring(5).replace('1', '*').replace('0', ' ');
    }

    // This is manual test check if console output contains default fonts -> 0-9, A-F
    @Ignore
    @Test
    public void Test_fonts_are_placed_correctly_in_memory() throws Exception {
        Chip8 chip8 = new Chip8();
        int currentHeight = 1;
        for (byte b : chip8.memory) {
            System.out.println(convertByteToStarsAndSpaces(b));
            if (currentHeight++ % Chip8.FONT_HEIGHT == 0) {
                System.out.println();
                System.out.println();
            }
        }

    }
}
