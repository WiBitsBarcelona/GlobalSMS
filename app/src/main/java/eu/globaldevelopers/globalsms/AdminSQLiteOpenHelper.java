package eu.globaldevelopers.globalsms;

/**
 * Created by Usuario on 19/07/2017.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    public AdminSQLiteOpenHelper(Context context, String nombre, SQLiteDatabase.CursorFactory factory, int version) {

        super(context, nombre, factory, version);

    }

    @Override

    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table operaciones(id integer primary key AUTOINCREMENT, tipo text, cabecera text, terminal text, fecha text, hora text, resultado text, codigo text, operacion text, producto text, litros_aceptados text, litros text, total text, codigo_error text, error text)");
        db.execSQL("create table respuestas(id integer primary key AUTOINCREMENT, respuesta text)");

    }

    @Override

    public void onUpgrade(SQLiteDatabase db, int version1, int version2) {

        db.execSQL("drop table if exists operaciones");
        db.execSQL("drop table if exists respuestas");

        db.execSQL("create table operaciones(id integer primary key AUTOINCREMENT, tipo text, cabecera text, terminal text, fecha text, hora text, resultado text, codigo text, operacion text, producto text, litros_aceptados text, litros text, total text, codigo_error text, error text)");
        db.execSQL("create table respuestas(id integer primary key AUTOINCREMENT, respuesta text)");
    }

    public ArrayList<operacion> getAllOperaciones() {
        String query = "select * from operaciones group by codigo order by id desc";
        ArrayList<operacion> operaciones = new ArrayList<operacion>();
        SQLiteDatabase database = getReadableDatabase();
        Cursor c = database.rawQuery(query, null);
        if (c != null) {
            while (c.moveToNext()) {

                String titulo = c.getString(c.getColumnIndex("id"));
                String fechahora = c.getString(c.getColumnIndex("fecha")) + " " + c.getString(c.getColumnIndex("hora"));
                String codigo = c.getString(c.getColumnIndex("codigo"));
                //String producto = c.getString(c.getColumnIndex("producto"));
                //String litros = c.getString(c.getColumnIndex("litros"));
                //String estado = c.getString(c.getColumnIndex("resultado"));

                operacion op = new operacion();

                op.setTitulo(titulo);
                op.setFechahora(fechahora);
                op.setCodigo(codigo);
                //op.setProducto(producto);
                //op.setLitros(litros);
                //op.setEstado(estado);

                operaciones.add(op);
            }
        }

        return operaciones;

    }

}
