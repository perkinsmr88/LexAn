import java.util.ArrayList;

/**
 * Created by Drivenwanderer on 4/2/14.
 */
public class SymTable
{

    ArrayList<Declaration> stacker = new ArrayList<Declaration>();

    //--------------------------------------------------------------------------------------------------

    public SymTable ()
    {
      //blank slate
    }

    //--------------------------------------------------------------------------------------------------

    public Declaration lookUp(int x)
    {
       Declaration dec = stacker.get(x);

        return dec;
    }

    //--------------------------------------------------------------------------------------------------

    public void insert(Declaration x)
    {
        stacker.add(x);
    }

    //--------------------------------------------------------------------------------------------------

    public void delete()
    {

    }

    //--------------------------------------------------------------------------------------------------

}
