# xls2pojo

Represent your MS Excel (XLS) rows into Java objects.

### POJO class mapping snippet

    @Row({
    	@Marker(value="Account", cell = @Cell("C")),
    	@Marker(value="(\\d+(?:\\.\\d+)?)", regex=true, cell = @Cell("A"))
    })
    public class Account {
    
    	@Cell("A")
    	private Integer id;
    
	    @Cell("B")
	    private String name;
	    
	    @Cell(value = "D", format="dd-MMM-yyyy")
	    private Date contractStartDate;

	    @Cell(value = "E", format="dd-MMM-yyyy")
	    private Date contractEndDate;
      
Download the source code to see the complete implementation of the test case.
