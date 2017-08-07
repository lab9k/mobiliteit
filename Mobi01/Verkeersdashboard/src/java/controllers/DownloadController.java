package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

@ApplicationScoped
@ManagedBean
public class DownloadController {
    
    /**
     * Lets the client download a file named file.type. Used for donwloading
     * the JSON- and XML-schemas
     * @param file
     * @param type
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public void download(String file, String type) throws FileNotFoundException, IOException{
    FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();
        
        externalContext.responseReset();
        externalContext.setResponseContentType("application/"+type);
        externalContext.setResponseHeader("Content-Disposition", "attachment;filename=\"" + file + "." + type + "\"");
        
        try (FileInputStream inputStream = new FileInputStream(new File(externalContext.getRealPath("/")+"schemas" + File.separatorChar + file + "." +type))) {
            OutputStream outputStream = externalContext.getResponseOutputStream();
            
            byte[] buffer = new byte[1024];
            int length;
            while((length=inputStream.read(buffer))>0)
                outputStream.write(buffer, 0, length);
        }
        context.responseComplete();
    }
    
}
