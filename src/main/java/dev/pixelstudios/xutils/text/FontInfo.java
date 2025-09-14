package dev.pixelstudios.xutils.text;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum FontInfo {

    A('A', 5), a('a', 5), A_SMALL('ᴀ', 5),
    B('B', 5), b('b', 5), B_SMALL('ʙ', 5),
    C('C', 5), c('c', 5), C_SMALL('ᴄ', 5),
    D('D', 5), d('d', 5), D_SMALL('ᴅ', 5),
    E('E', 5), e('e', 5), E_SMALL('ᴇ', 5),
    F('F', 5), f('f', 4), F_SMALL('ꜰ', 5),
    G('G', 5), g('g', 5), G_SMALL('ɢ', 5),
    H('H', 5), h('h', 5), H_SMALL('ʜ', 5),
    I('I', 3), i('i', 1), I_SMALL('ɪ', 3),
    J('J', 5), j('j', 5), J_SMALL('ᴊ', 5),
    K('K', 5), k('k', 4), K_SMALL('ᴋ', 5),
    L('L', 5), l('l', 2), L_SMALL('ʟ', 5),
    M('M', 5), m('m', 5), M_SMALL('ᴍ', 5),
    N('N', 5), n('n', 5), N_SMALL('ɴ', 5),
    O('O', 5), o('o', 5), O_SMALL('ᴏ', 5),
    P('P', 5), p('p', 5), P_SMALL('ᴘ', 5),
    Q('Q', 5), q('q', 5), Q_SMALL('ǫ', 5),
    R('R', 5), r('r', 5), R_SMALL('ʀ', 5),
    S('S', 5), s('s', 5), S_SMALL('s', 5),
    T('T', 5), t('t', 3), T_SMALL('ᴛ', 5),
    U('U', 5), u('u', 5), U_SMALL('ᴜ', 5),
    V('V', 5), v('v', 5), V_SMALL('ᴠ', 5),
    W('W', 5), w('w', 5), W_SMALL('ᴡ', 5),
    X('X', 5), x('x', 5), X_SMALL('x', 5),
    Y('Y', 5), y('y', 5), Y_SMALL('ʏ', 5),
    Z('Z', 5), z('z', 5), Z_SMALL('ᴢ', 5),

    NUM_1('1', 5),
    NUM_2('2', 5),
    NUM_3('3', 5),
    NUM_4('4', 5),
    NUM_5('5', 5),
    NUM_6('6', 5),
    NUM_7('7', 5),
    NUM_8('8', 5),
    NUM_9('9', 5),
    NUM_0('0', 5),

    EXCLAMATION_POINT('!', 1),
    AT_SYMBOL('@', 6),
    NUM_SIGN('#', 5),
    DOLLAR_SIGN('$', 5),
    PERCENT('%', 5),
    UP_ARROW('^', 5),
    AMPERSAND('&', 5),
    ASTERISK('*', 5),
    LEFT_PARENTHESIS('(', 4),
    RIGHT_PARENTHESIS(')', 4),
    MINUS('-', 5),
    UNDERSCORE('_', 5),
    PLUS_SIGN('+', 5),
    EQUALS_SIGN('=', 5),
    LEFT_CURL_BRACE('{', 4),
    RIGHT_CURL_BRACE('}', 4),
    LEFT_BRACKET('[', 3),
    RIGHT_BRACKET(']', 3),
    COLON(':', 1),
    SEMI_COLON(';', 1),
    DOUBLE_QUOTE('"', 3),
    SINGLE_QUOTE('\'', 1),
    LEFT_ARROW('<', 4),
    RIGHT_ARROW('>', 4),
    QUESTION_MARK('?', 5),
    SLASH('/', 5),
    BACK_SLASH('\\', 5),
    LINE('|', 1),
    TILDE('~', 5),
    TICK('`', 2),
    PERIOD('.', 1),
    COMMA(',', 1),
    SPACE(' ', 3),

    DEFAULT('a', 5);

    private static final FontInfo[] VALUES = values();

    private final char character;
    private final int length;

    public int getBoldLength() {
        if (this == FontInfo.SPACE) {
            return getLength();
        }
        return length + 1;
    }

    public static FontInfo getFontInfo(char character) {
        return Arrays.stream(VALUES).filter(font -> font.getCharacter() == character).findAny().orElse(FontInfo.DEFAULT);
    }

    public static int getLength(String text) {
        int length = 0;
        for (char c : text.toCharArray()) {
            length += FontInfo.getFontInfo(c).getLength();
        }
        return length;
    }

}
