import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/* 
 * Credit: https://www.youtube.com/watch?v=IznTKbLoWG0
 * Description: limits user input into JTextField to only a single number from 1-9.
 */
public class JTextFieldCharLimit extends PlainDocument {
    //character limit
    private int limit;

    public JTextFieldCharLimit (int limitation) { 
        this.limit = limitation; 
    }

    public void insertString (int offset, String str, AttributeSet set) throws BadLocationException {
        if (str == null) return;
        
        if ((getLength() + str.length()) <= limit) {
            try { 
                //make sure input is an integer
                int n = Integer.parseInt(str);
                //treat 0s as empty cells in the Sudoku board
                if (n == 0) return;
                super.insertString(offset, str, set);
            } catch (NumberFormatException e) {
                return;
            }
        }

    }
}
