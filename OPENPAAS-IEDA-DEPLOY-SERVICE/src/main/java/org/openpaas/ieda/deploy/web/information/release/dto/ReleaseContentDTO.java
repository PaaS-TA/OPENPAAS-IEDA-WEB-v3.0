package org.openpaas.ieda.deploy.web.information.release.dto;

import org.hibernate.validator.constraints.NotBlank;

public class ReleaseContentDTO {
    
    public static class Upload{
        @NotBlank
        private String fileName; //릴리즈 파일명

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
    }
    
    public static class Delete{
        @NotBlank
        private String fileName; //릴리즈명
        
        @NotBlank
        private String version; //릴리즈 버전

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }
    
}
