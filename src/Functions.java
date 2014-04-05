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

    public Functions()
    {
        /* create an empty one */
    }

    public void loadName(String x)
    {
        name = x;
    }

    public void loadType(String x)
    {
        type = x;
    }

    public void loadIndex(boolean x)
    {
        isArray.add(x);
    }

    public void loadpType(String x)
    {
        pType.add(x);
    }

    public void loadpName(String x)
    {
        pName.add(x);
    }

    public void loadrType(String x)
    {
        rType = x;
    }
}
