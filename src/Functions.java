import java.util.ArrayList;

/**
 * Created by Drivenwanderer on 4/2/14.
 */
public class Functions
{
    String name;
    String type;
    String rType;
    ArrayList<Boolean> isArray = new ArrayList<Boolean>();
    ArrayList<String> pType = new ArrayList<String>();
    ArrayList<String> pName = new ArrayList<String>();

    public Functions(String x, String y, ArrayList<String> pt, ArrayList<String> pn, boolean isAr)
    {
        name = x;
        type = y;
        rType = y;

        if(pt.size() == 1)
        {
            pType.add(pt.get(0));
        }
        else
        {
            for (int i = 0; i < pt.size() - 1; i++)
            {
                pType.add(pt.get(i));
            }
        }

        if(pn.size() == 1)
        {
            pName.add(pt.get(0));
        }
        else
        {
            for (int i = 0; i < pn.size() - 1; i++)
            {
                pName.add(pn.get(i));
            }
        }

        isArray.add(isAr);
    }

}
