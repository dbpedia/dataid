
import org.aksw.dataid.wrapper.InternalLieteralImpl;
import org.junit.Test;

import java.util.Date;

/**
 * Created by Chile on 3/12/2015.
 */
public class VirtuosoTestSuite
{
    public static void main(String[] args) {
        InternalLieteralImpl lit = new InternalLieteralImpl();
        lit.setDatatype("http://www.w3.org/2001/XMLSchema#decimal");
        lit.setLabel("33.5");
        Float erg = lit.<Float>value();
        System.out.println(erg.getClass() + " - " + erg);

        lit = new InternalLieteralImpl();
        lit.setDatatype("http://www.w3.org/2001/XMLSchema#boolean");
        lit.setLabel("0");
        System.out.println(lit.getDatatype() + " - " + lit.<Boolean>value());
        lit = new InternalLieteralImpl();
        lit.setDatatype("http://www.w3.org/2001/XMLSchema#time");
        lit.setLabel("2015-09-22");
        System.out.println(lit.getDatatype() + " - " + lit.<Date>value());
        lit = new InternalLieteralImpl();
        lit.setDatatype("http://www.w3.org/2001/XMLSchema#long");
        lit.setLabel("5723862234263332323");
        System.out.println(lit.getDatatype() + " - " + lit.<Long>value());
        lit = new InternalLieteralImpl();
        lit.setDatatype("http://www.w3.org/2001/XMLSchema#nonPositiveInteger");
        lit.setLabel("5723862234263332323");
        System.out.println(lit.getDatatype() + " - " + lit.<Integer>value());
        lit = new InternalLieteralImpl();
        lit.setDatatype("http://www.w3.org/2001/XMLSchema#byte");
        lit.setLabel("5");
        System.out.println(lit.getDatatype() + " - " + lit.<Byte>value());
        lit = new InternalLieteralImpl();
        lit.addTranslation("sggsgrhtew", "xx");
        lit.addTranslation("sgtdhth", "de");
        lit.addTranslation("rtetwetwr", "ga");
        lit.addTranslation("terziuuzrtgw", "it");
        System.out.println(lit.getDatatype() + " - " + lit.<String>value());
        System.out.println("it:" + " - " + lit.getLabel("it"));
        System.out.println("xx:" + " - " + lit.getLabel("xx"));
    }

    @Test
    public void testLiterals() throws Exception {


    }
}
