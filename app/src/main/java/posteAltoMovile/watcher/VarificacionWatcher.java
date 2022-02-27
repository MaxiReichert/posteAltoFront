package posteAltoMovile.watcher;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class VarificacionWatcher implements TextWatcher {

    private EditText editText;
    private EditText editTextVerif;

    public VarificacionWatcher(EditText editText, EditText editTextVerif) {
        this.editText = editText;
        this.editTextVerif = editTextVerif;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String text = editable.toString();

        if(editText.getText().toString().equals(text)){
            editTextVerif.setError(null);
        }
        else{
            editTextVerif.setError("Los campos no coinciden");
        }
    }
}
