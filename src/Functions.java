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

    public Functions(String x, String y, int z)
    {
        name  = x;
        type  = y;
        index = z;
    }
}
