package eu.globaldevelopers.globalsms;

/**
 * Created by Artur on 07/11/2017.
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class AdaptadorDeOperaciones extends BaseAdapter {
    private Context context;
    ArrayList<operacion> operList;
    private static LayoutInflater inflater = null;

    public AdaptadorDeOperaciones(Context context, ArrayList<operacion> operList) {
        this.context = context;
        this.operList = operList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return operList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null)
            convertView = inflater.inflate(R.layout.copia_grid, null);

        //TextView tituloTextView = (TextView) convertView.findViewById(R.id.titulo);
        TextView fechahoraTextView = (TextView) convertView.findViewById(R.id.fechahora);
        TextView codigoTextView = (TextView) convertView.findViewById(R.id.codigo);
        //TextView productoTextView = (TextView) convertView.findViewById(R.id.producto);
        //TextView litrosTextView = (TextView) convertView.findViewById(R.id.litros);
        //TextView estadoTextView = (TextView) convertView.findViewById(R.id.estado);

        operacion e = new operacion();
        e = operList.get(position);
        //tituloTextView.setText(e.getTitulo());
        fechahoraTextView.setText(e.getFechahora());
        codigoTextView.setText(e.getCodigo());
        //productoTextView.setText(e.getProducto());
        //litrosTextView.setText(e.getLitros());
        //estadoTextView.setText(e.getEstado());
        return convertView;

    }
}
