package support;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cellaflora.muni.MainActivity;
import com.cellaflora.muni.R;

/**
 * Created by sdickson on 8/10/13.
 */
public class ContactListItem extends RelativeLayout
{
    Context context;
    LayoutInflater inflater;
    TextView txtName;
    ImageView actionCancel;

    public ContactListItem(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.contact_list_item, this);
        txtName = (TextView) itemView.findViewById(R.id.contact_list_item_name);
        txtName.setTypeface(MainActivity.myriadProSemiBold);
        actionCancel = (ImageView) itemView.findViewById(R.id.contact_list_item_cancel);
    }

    public void setCancelEnabled(boolean enabled)
    {
        if(enabled)
        {
            actionCancel.setVisibility(View.VISIBLE);
        }
        else
        {
            actionCancel.setVisibility(View.GONE);
        }
    }

    public void setName(String name)
    {
        txtName.setText(name);
    }

    public String getName()
    {
        return txtName.getText().toString();
    }

}
