package cpu;

import java.util.Arrays;

/**
 * Created by kinder112 on 25.06.2016.
 */
public class Chip8 {

    public static final int MEMORY_SIZE = 4096;
    public static final int FONT_HEIGHT = 5;
    public static final int FONT_WIDTH = 8; // stored in 1 byte
    public static final int FONTS_NUMBER = 16;

    static final byte FONTS[] = {
            (byte) 0xF0, (byte) 0x90, (byte) 0x90, (byte) 0x90, (byte) 0xF0, // 0
            (byte) 0x20, (byte) 0x60, (byte) 0x20, (byte) 0x20, (byte) 0x70, // 1
            (byte) 0xF0, (byte) 0x10, (byte) 0xF0, (byte) 0x80, (byte) 0xF0, // 2
            (byte) 0xF0, (byte) 0x10, (byte) 0xF0, (byte) 0x10, (byte) 0xF0, // 3
            (byte) 0x90, (byte) 0x90, (byte) 0xF0, (byte) 0x10, (byte) 0x10, // 4
            (byte) 0xF0, (byte) 0x80, (byte) 0xF0, (byte) 0x10, (byte) 0xF0, // 5
            (byte) 0xF0, (byte) 0x80, (byte) 0xF0, (byte) 0x90, (byte) 0xF0, // 6
            (byte) 0xF0, (byte) 0x10, (byte) 0x20, (byte) 0x40, (byte) 0x40, // 7
            (byte) 0xF0, (byte) 0x90, (byte) 0xF0, (byte) 0x90, (byte) 0xF0, // 8
            (byte) 0xF0, (byte) 0x90, (byte) 0xF0, (byte) 0x10, (byte) 0xF0, // 9
            (byte) 0xF0, (byte) 0x90, (byte) 0xF0, (byte) 0x90, (byte) 0x90, // A
            (byte) 0xE0, (byte) 0x90, (byte) 0xE0, (byte) 0x90, (byte) 0xE0, // B
            (byte) 0xF0, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0xF0, // C
            (byte) 0xE0, (byte) 0x90, (byte) 0x90, (byte) 0x90, (byte) 0xE0, // D
            (byte) 0xF0, (byte) 0x80, (byte) 0xF0, (byte) 0x80, (byte) 0xF0, // E
            (byte) 0xF0, (byte) 0x80, (byte) 0xF0, (byte) 0x80, (byte) 0x80};// F


    byte memory[] = new byte[MEMORY_SIZE];
    byte registers[] = new byte[16];
    short I;
    short programCounter;
    byte delayTimer;
    byte soundTimer;
    byte stackPointer;
    short stack[] = new short[16];
    boolean clearScreen;
    Opcodes opcodes = new Opcodes(this);


    {
        System.arraycopy(FONTS, 0, memory, 0, FONTS_NUMBER * FONT_HEIGHT);
    }

    public static void main(String[] args) {
        Chip8 chip = new Chip8();
        System.out.println(Arrays.toString(chip.memory));
    }

    public void handleOpcode(short opcode) {
        opcodes.handleOpcode(opcode);
    }
}
