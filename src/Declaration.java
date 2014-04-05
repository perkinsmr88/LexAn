/**
 * Created by Drivenwanderer on 3/22/14.
 */
public class Declaration
{
   String ID;
   String type;
   int arraySize;
   boolean flag;

    public Declaration()
    {
        //create a blank slate
    }

    public void fillID(String x)
    {
        ID = x;
    }

    public void fillType(String y)
    {
        type = y;
    }

    public void fillSize(int z)
    {
        arraySize = z;
    }

    public void setFlag(boolean f)
    {
        flag = f;
    }


}
