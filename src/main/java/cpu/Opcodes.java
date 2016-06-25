package cpu;

/**
 * Created by kinder112 on 25.06.2016.
 */
public class Opcodes {

    private final Chip8 cpu;

    public Opcodes(Chip8 cpu) {
        this.cpu = cpu;
    }

//
//
//
//    00EE - RET
//    Return from a subroutine.
//
//    The interpreter sets the program counter to the address at the top of the stack, then subtracts 1 from the stack pointer.

    void handleOpcode(short opcode){
        switch (opcode & 0xF000) {
            case 0x0000:
                switch (opcode) {
                    // 00E0 - CLS
                    // Clear the display.
                    case 0x00E0:
                        cpu.clearScreen = true;
                        cpu.programCounter+=2;
                        break;
                    // 00EE - RET
                    // Return from a subroutine.
                    // The interpreter sets the program counter to the address at the top of the stack, then subtracts 1 from the stack pointer.
                    case 0x00EE:
                        cpu.programCounter = cpu.stack[--cpu.stackPointer];
                        cpu.programCounter+=2;
                        break;
                    // 0nnn - SYS addr
                    // Jump to a machine code routine at nnn.
                    // This instruction is only used on the old computers on which Chip-8 was originally implemented. It is ignored by modern interpreters.
                    default:
                        throw new UnsupportedOperationException("0NNN is unsupported on this interpreter");
                }

        }
    }
}
