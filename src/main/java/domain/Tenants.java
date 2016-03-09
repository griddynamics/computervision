package domain;

/**
 * Enumeration of valid {@link Tenants}, originally introduced with respect to tenancy specifics observed by the
 * attribute service.
 *
 * @author ksmirnov@griddynamics.com
 */
public enum Tenants {
    MCOM, BCOM;

    public static Tenants findByString(String text)
    {
        for(Tenants tenant : Tenants.values())
        {
            if(tenant.name().equalsIgnoreCase(text))
            {
                return tenant;
            }
        }
        return null;
    }
}
