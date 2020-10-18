import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
// https://www.youtube.com/watch?v=IznTKbLoWG0
public class JTextFieldCharLimit extends PlainDocument {

    private static final long serialVersionUID = 1L;
    private int limit;

    public JTextFieldCharLimit (int limitation) { 
        this.limit = limitation; 
    }

    public void insertString (int offset, String str, AttributeSet set) throws BadLocationException {
        if (str == null) return;
        
        if ((getLength() + str.length()) <= limit) {
            //str = str.toUpperCase();
            try { 
                //make sure input is an integer
                int n = Integer.parseInt(str);
                super.insertString(offset, str, set);
            } catch (NumberFormatException e) {
                return;
            }
        }

    }
}
