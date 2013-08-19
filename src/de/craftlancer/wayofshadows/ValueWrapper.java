package de.craftlancer.wayofshadows;

public class ValueWrapper
{
    private String input;
    private Double value;
    
    public ValueWrapper(String string)
    {
        input = string;
        value = Double.valueOf(string);
    }
    
    public ValueWrapper(double d)
    {
        value = d;
        input = String.valueOf(d);
    }
    
    public double getValue(int level)
    {
        if (value != null)
            return value;
        else
            return Double.valueOf(getMathResult(input, level, input)).doubleValue();
    }
    
    public double getValue(int level, int int2)
    {
        if (value != null)
            return value;
        else
            return Double.valueOf(getMathResult(input, level, int2));
    }
    
    public int getIntValue(int level)
    {
        if (value != null)
            return value.intValue();
        else
            return Double.valueOf(getMathResult(input, level, input)).intValue();
    }
    
    public String getInput()
    {
        return input;
    }
    
    private String getMathResult(String form, double x, double y)
    {
        form = form.replace("y", String.valueOf(y));
        return getMathResult(form, x, form);
    }
    
    private String getMathResult(String form, double x, String completForm)
    {
        if (x <= 0)
            return "0";
        
        form = form.replace("x", String.valueOf(x));
        form = form.replaceAll("[-]", "+-");
        
        while (form.contains("(") && form.contains(")"))
        {
            int indexFirst = -1;
            int indexLast = -1;
            int countOpen = 0;
            for (int i = 0; i < form.toCharArray().length; i++)
            {
                if (form.toCharArray()[i] == '(')
                    if (indexFirst == -1)
                        indexFirst = i;
                    else
                        countOpen++;
                
                if (form.toCharArray()[i] == ')')
                    if (indexLast == -1 && countOpen == 0)
                        indexLast = i;
                    else
                        countOpen--;
            }
            
            if (indexFirst != -1 && indexLast != -1)
                form = form.replace(form.substring(indexFirst, indexLast + 1), getMathResult(form.substring(indexFirst + 1, indexLast), x, completForm));
        }
        
        String[] array = form.split("[+]");
        
        for (String s : array)
            if (s.contains("*"))
                form = form.replace(s, String.valueOf(Double.valueOf(getMathResult(s.substring(0, s.indexOf("*")), x, completForm)) * Double.valueOf(getMathResult(s.substring(s.indexOf("*") + 1), x, completForm))));
            else if (s.contains("/"))
                form = form.replace(s, String.valueOf(Double.valueOf(s.substring(0, s.indexOf("/"))) / Double.valueOf(getMathResult(s.substring(s.indexOf("/") + 1), x, completForm))));
            else if (s.contains("log"))
                form = form.replace(s, String.valueOf(Math.log(Double.valueOf(getMathResult(s.replace("log", ""), x, completForm)))));
            else if (s.contains("sqrt"))
                form = form.replace(s, String.valueOf(Math.sqrt(Double.valueOf(getMathResult(s.replace("sqrt", ""), x, completForm)))));
            else if (s.contains("^"))
                form = form.replace(s, String.valueOf(Math.pow(Double.valueOf(getMathResult(s.substring(0, s.indexOf("^")), x, completForm)), Double.valueOf(getMathResult(s.substring(s.indexOf("^") + 1), x, completForm)))));
        
        double result = 0;
        for (String s : form.split("[+]"))
            try
            {
                result += Double.valueOf(s);
            }
            catch (NumberFormatException e)
            {
                result += 0;
            }
        
        return String.valueOf(result);
    }
}
