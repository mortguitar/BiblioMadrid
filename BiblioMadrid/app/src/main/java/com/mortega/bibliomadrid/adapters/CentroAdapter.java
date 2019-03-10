package com.mortega.bibliomadrid.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mortega.bibliomadrid.R;
import com.mortega.bibliomadrid.activities.MainActivity;
import com.mortega.bibliomadrid.base.Centro;

import java.util.List;

public class CentroAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private int idLayout;
    private List<Centro> centros;

    public CentroAdapter(MainActivity contexto, int idLayout,
                         List<Centro> centros) {

        inflater = LayoutInflater.from(contexto);
        this.idLayout = idLayout;
        this.centros = centros;
    }

    static class ViewHolder {

        TextView tvNombre;
        TextView tvDescripcion;
        TextView tvPrecio;
    }

    @Override
    public View getView(int posicion, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (convertView == null) {
            convertView = inflater.inflate(idLayout, null);

            holder = new ViewHolder();
            holder.tvNombre = (TextView) convertView.findViewById(R.id.tvNombre);
            holder.tvDescripcion = (TextView) convertView.findViewById(R.id.tvDescripcion);
            holder.tvPrecio = (TextView) convertView.findViewById(R.id.tvPrecio);

            convertView.setTag(holder);
        }
        else {

            holder = (ViewHolder) convertView.getTag();
        }

        Centro centro = centros.get(posicion);
        holder.tvNombre.setText(centro.getNombre());
        holder.tvDescripcion.setText(centro.getDireccion());
        holder.tvPrecio.setText(centro.getOrganizacion());

        return convertView;
    }

    @Override
    public int getCount() {
        return centros.size();
    }

    @Override
    public Object getItem(int posicion) {
        return centros.get(posicion);
    }

    @Override
    public long getItemId(int posicion) {
        return centros.get(posicion).getId();
    }
}
