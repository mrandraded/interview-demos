package com.quickbase.devint;

public interface DBManager {
    public void open();
    public void close();
    
    public boolean isOpen();
    
    public boolean requiresRefresh();
    
    public void selectCountry(String countryName);
    
    public boolean isDataPresentForSelection();
    public Integer getPopulationOfSelection();
    
    public static DBManager createDataBaseConnection() 
    {
        return new DBManagerImpl();
    }
}
