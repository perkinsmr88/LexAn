import java.util.ArrayDeque;

/**
 * Created by Drivenwanderer on 4/2/14.
 */
public class Functions
{
    String name;
    String type;
    int index;
    ArrayDeque<String> pType = new ArrayDeque<String>();
    ArrayDeque<String> pName = new ArrayDeque<String>();

    public Functions()
    {
        //create an empty one
    }

    public void loadName(String x)
    {
        name = x;
    }

    public void loadType(String x)
    {
        type = x;
    }

    public void loadIndex(int x)
    {
        index = x;
    }

    public void loadpType(String x)
    {
        pType.add(x);
    }

    public void loadpName(String x)
    {
        pName.add(x);
    }
}
