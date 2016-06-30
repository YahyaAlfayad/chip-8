package cpu;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by kinder112 on 25.06.2016.
 */
public class Chip8Test {

    private Chip8 cpu;

    @Before
    public void setUp() throws Exception {
        cpu = new Chip8();
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

    private String convertByteToStarsAndSpaces(byte b) {
        //Only high nibble (4 bytes) are used to represent font
        return Integer.toBinaryString((b >> 4) & 0xF | 0x100)
                .substring(5).replace('1', '*').replace('0', ' ');
    }

    // 0NNN  Calls RCA 1802 program at address NNN. Not necessary for most ROMs.
    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowUnsupportedOperationExceptionOn_0NNN() throws Exception {
        cpu.handleOpcode((short) 0x0EEE);
    }

    // 00E0	Clears the screen.
    @Test
    public void shouldSetClearScreenFlagOn_00E0() throws Exception {
        // When
        cpu.handleOpcode((short) 0x00E0);

        // Then
        assertTrue(cpu.clearScreen);
    }

    // 00EE	Returns from a subroutine.
    @Test
    public void shouldDecreaseStackPointerAndSetProgramCounterToPreviousAddressOn_00EE() throws Exception {
        //Given
        final int previousFrameAddress = 0xABCD;
        cpu.stack[cpu.stackPointer++] = (short) previousFrameAddress;
        //When
        cpu.handleOpcode((short) 0x00EE);
        //Then
        assertThat(cpu.stackPointer, is((byte) 0));
        assertThat(cpu.programCounter, is((short) previousFrameAddress));


    }

    // 1NNN	Jumps to address NNN.
    @Test
    public void shouldJumpToAddressNNNOn_1NNN() throws Exception {
        //Given
        final int opCodeWithJumpAddress = 0x1BAB;
        //When
        cpu.handleOpcode((short) opCodeWithJumpAddress);
        //Then
        assertThat(cpu.programCounter, is((short) 0xBAB));
    }

    // 2NNN	Calls subroutine at NNN.
    @Test
    public void shouldCallSubroutineAtNNNOn_2NNN() throws Exception {
        //Given
        int opCodeWithJumpAddress = 0x2BAC;
        int previousStackPointer = 5;
        int previousPC = 0x0CDE;

        cpu.stackPointer = (byte) previousStackPointer;
        cpu.programCounter = (short) previousPC;
        //When
        cpu.handleOpcode((short) opCodeWithJumpAddress);
        //Then
        assertThat(cpu.stackPointer, is((byte) ++previousStackPointer));
        assertThat(cpu.stack[cpu.stackPointer-1], is((short) (++previousPC)));
        assertThat(cpu.programCounter, is( (short) (opCodeWithJumpAddress & 0x0FFF)));
    }

    // 3XNN	Skips the next instruction if VX equals NN.
    @Test
    public void shouldSkipNextInstructionOn_3XNN() throws Exception {
        //Given
        final int instruction = 0x37CA;
        final int vX = 0xCA;
        final int x = 7;
        int pc = 0x5;

        cpu.programCounter = (short) pc;
        cpu.registers[x] = (byte) vX;
        //When
        cpu.handleOpcode((short) instruction);
        //Then
        assertThat(cpu.programCounter, is(pc + 2));
    }

    // 3XNN	Skips the next instruction if VX equals NN.
    @Test
    public void shouldNotSkipNextInstructionOn_3XNN() throws Exception {
        //Given
        final int instruction = 0x37CA;
        final int vX = 0xCB;
        final int x = 7;
        int pc = 0x5;

        cpu.programCounter = (short) pc;
        cpu.registers[x] = (byte) vX;
        //When
        cpu.handleOpcode((short) instruction);
        //Then
        assertThat(cpu.programCounter, is(pc + 1));
    }

    // 4XNN	Skips the next instruction if VX doesn't equal NN.
    @Test
    public void shouldSkipNextInstructionOn_4XNN() throws Exception {
        //Given
        final int instruction = 0x47CA;
        final int vX = 0xCF;
        final int x = 7;
        int pc = 0x5;

        cpu.programCounter = (short) pc;
        cpu.registers[x] = (byte) vX;
        //When
        cpu.handleOpcode((short) instruction);
        //Then
        assertThat(cpu.programCounter, is(pc + 2));
    }

    // 4XNN	Skips the next instruction if VX equals NN.
    @Test
    public void shouldNotSkipNextInstructionOn_4XNN() throws Exception {
        //Given
        final int instruction = 0x47CA;
        final int vX = 0xCA;
        final int x = 7;
        int pc = 0x5;

        cpu.programCounter = (short) pc;
        cpu.registers[x] = (byte) vX;
        //When
        cpu.handleOpcode((short) instruction);
        //Then
        assertThat(cpu.programCounter, is(pc + 1));
    }
    // 5XY0	Skips the next instruction if VX equals VY.

    @Test
    public void shouldSkipNextInstructionOn_5XY0() throws Exception {
        //Given
        final int instruction = 0x5480;
        final int x = 4;
        final int y = 8;
        final int value = 0xBABE;
        final int pc = 0xAFFF;

        cpu.registers[x] = (byte) value;
        cpu.registers[y] = (byte) value;
        cpu.programCounter = (short) pc;
        //When
        cpu.handleOpcode((short) instruction);
        //Then
        assertThat(cpu.programCounter, is(pc + 2));
    }

    @Test
    public void shouldNotSkipNextInstructionOn_5XY0() throws Exception {
        //Given
        final int instruction = 0x5480;
        final int x = 4;
        final int y = 8;
        final int vX = 0xBABE;
        final int vY = 0xBABA;
        final int pc = 0xAFFF;

        cpu.registers[x] = (byte) vX;
        cpu.registers[y] = (byte) vY;
        cpu.programCounter = (short) pc;
        //When
        cpu.handleOpcode((short) instruction);
        //Then
        assertThat(cpu.programCounter, is(pc + 1));
    }


    // 6XNN	Sets VX to NN.
    // 7XNN	Adds NN to VX.
    // 8XY0	Sets VX to the value of VY.
    // 8XY1	Sets VX to VX or VY.
    // 8XY2	Sets VX to VX and VY.
    // 8XY3	Sets VX to VX xor VY.
    // 8XY4	Adds VY to VX. VF is set to 1 when there's a carry, and to 0 when there isn't.
    // 8XY5	VY is subtracted from VX. VF is set to 0 when there's a borrow, and 1 when there isn't.
    // 8XY6	Shifts VX right by one. VF is set to the value of the least significant bit of VX before the shift.[2]
    // 8XY7	Sets VX to VY minus VX. VF is set to 0 when there's a borrow, and 1 when there isn't.
    // 8XYE	Shifts VX left by one. VF is set to the value of the most significant bit of VX before the shift.[2]
    // 9XY0	Skips the next instruction if VX doesn't equal VY.
    // ANNN	Sets I to the address NNN.
    // BNNN	Jumps to the address NNN plus V0.
    // CXNN	Sets VX to the result of a bitwise and operation on a random number and NN.
    // DXYN	Sprites stored in memory at location in index register (I), 8bits wide. Wraps around the screen. If when drawn, clears a pixel, register VF is set to 1 otherwise it is zero. All drawing is XOR drawing (i.e. it toggles the screen pixels). Sprites are drawn starting at position VX, VY. N is the number of 8bit rows that need to be drawn. If N is greater than 1, second line continues at position VX, VY+1, and so on.
    // EX9E	Skips the next instruction if the key stored in VX is pressed.
    // EXA1	Skips the next instruction if the key stored in VX isn't pressed.
    // FX07	Sets VX to the value of the delay timer.
    // FX0A	A key press is awaited, and then stored in VX.
    // FX15	Sets the delay timer to VX.
    // FX18	Sets the sound timer to VX.
    // FX1E	Adds VX to I.[3]
    // FX29	Sets I to the location of the sprite for the character in VX. Characters 0-F (in hexadecimal) are represented by a 4x5 font.
    // FX33	Stores the binary-coded decimal representation of VX, with the most significant of three digits at the address in I, the middle digit at I plus 1, and the least significant digit at I plus 2. (In other words, take the decimal representation of VX, place the hundreds digit in memory at location in I, the tens digit at location I+1, and the ones digit at location I+2.)
    // FX55	Stores V0 to VX (including VX) in memory starting at address I.[4]
    // FX65	Fills V0 to VX (including VX) with values from memory starting at address I.[4]
}