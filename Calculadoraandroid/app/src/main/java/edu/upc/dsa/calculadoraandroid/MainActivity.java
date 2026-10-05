package edu.upc.dsa.calculadoraandroid;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private TextView pantalla;
    private CheckBox grados;

    private double primerNumero = 0;
    private String operacion = "";
    private boolean nuevaEntrada = true;
    private boolean esperandoNumero = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (vista, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int margen = (int) (16 * getResources().getDisplayMetrics().density);

            vista.setPadding(
                    barras.left + margen,
                    barras.top + margen,
                    barras.right + margen,
                    barras.bottom + margen
            );

            return insets;
        });

        pantalla = findViewById(R.id.pantalla);
        grados = findViewById(R.id.grados);

        int[] numeros = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        };

        for (int id : numeros) {
            Button boton = findViewById(id);
            boton.setOnClickListener(v -> escribir(boton.getText().toString()));
        }

        findViewById(R.id.btnPunto).setOnClickListener(v -> escribir("."));

        findViewById(R.id.btnSumar).setOnClickListener(v -> elegirOperacion("+"));
        findViewById(R.id.btnRestar).setOnClickListener(v -> elegirOperacion("-"));
        findViewById(R.id.btnMultiplicar).setOnClickListener(v -> elegirOperacion("*"));
        findViewById(R.id.btnDividir).setOnClickListener(v -> elegirOperacion("/"));

        findViewById(R.id.btnIgual).setOnClickListener(v -> {
            if (!operacion.isEmpty() && !esperandoNumero) {
                calcular();
            }
        });

        findViewById(R.id.btnClear).setOnClickListener(v -> limpiar());

        findViewById(R.id.btnSin).setOnClickListener(v -> trigonometria("sin"));
        findViewById(R.id.btnCos).setOnClickListener(v -> trigonometria("cos"));
        findViewById(R.id.btnTan).setOnClickListener(v -> trigonometria("tan"));
    }

    private void escribir(String valor) {
        if (nuevaEntrada) {
            pantalla.setText("");
            nuevaEntrada = false;
        }

        String texto = pantalla.getText().toString();

        if (valor.equals(".")) {
            if (texto.contains(".")) {
                return;
            }

            if (texto.isEmpty()) {
                texto = "0";
            }
        }

        if (texto.equals("0") && !valor.equals(".")) {
            texto = "";
        }

        pantalla.setText(texto + valor);
        esperandoNumero = false;
    }

    private boolean hayNumero() {
        String texto = pantalla.getText().toString();
        return !texto.isEmpty() && !texto.equals("Error");
    }

    private void elegirOperacion(String nuevaOperacion) {
        if (!hayNumero()) {
            return;
        }

        if (!operacion.isEmpty() && !esperandoNumero) {
            if (!calcular()) {
                return;
            }
        }

        primerNumero = Double.parseDouble(pantalla.getText().toString());
        operacion = nuevaOperacion;
        nuevaEntrada = true;
        esperandoNumero = true;
    }

    private boolean calcular() {
        if (!hayNumero()) {
            return false;
        }

        double segundoNumero = Double.parseDouble(pantalla.getText().toString());
        double resultado;

        if (operacion.equals("/") && segundoNumero == 0) {
            mostrarError();
            return false;
        }

        switch (operacion) {
            case "+":
                resultado = primerNumero + segundoNumero;
                break;
            case "-":
                resultado = primerNumero - segundoNumero;
                break;
            case "*":
                resultado = primerNumero * segundoNumero;
                break;
            case "/":
                resultado = primerNumero / segundoNumero;
                break;
            default:
                resultado = segundoNumero;
                break;
        }

        operacion = "";
        esperandoNumero = false;

        return mostrarResultado(resultado);
    }

    private void trigonometria(String tipo) {
        if (!hayNumero() || esperandoNumero) {
            return;
        }

        double angulo = Double.parseDouble(pantalla.getText().toString());

        if (grados.isChecked()) {
            angulo = Math.toRadians(angulo);
        }

        if (tipo.equals("tan") && Math.abs(Math.cos(angulo)) < 0.000000000001) {
            mostrarError();
            return;
        }

        double resultado;

        switch (tipo) {
            case "sin":
                resultado = Math.sin(angulo);
                break;
            case "cos":
                resultado = Math.cos(angulo);
                break;
            default:
                resultado = Math.tan(angulo);
                break;
        }

        mostrarResultado(resultado);
    }

    private boolean mostrarResultado(double resultado) {
        if (Double.isNaN(resultado) || Double.isInfinite(resultado)) {
            mostrarError();
            return false;
        }

        pantalla.setText(String.valueOf(resultado));
        nuevaEntrada = true;
        return true;
    }

    private void limpiar() {
        pantalla.setText("");
        primerNumero = 0;
        operacion = "";
        nuevaEntrada = true;
        esperandoNumero = false;
    }

    private void mostrarError() {
        limpiar();
        pantalla.setText("Error");
    }
}