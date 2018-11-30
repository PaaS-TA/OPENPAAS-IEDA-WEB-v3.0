package org.openpaas.ieda.hbdeploy.web.information.stemcell.dto;

import org.hibernate.validator.constraints.NotBlank;

public class HbStemcellDTO {
    public static class Upload {
        @NotBlank
        private String fileName; //파일명
        private String directorId;
        public String getFileName() {
            return fileName;
        }
        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
        public String getDirectorId() {
            return directorId;
        }
        public void setDirectorId(String directorId) {
            this.directorId = directorId;
        }
    }
    
    public static class Delete {
        @NotBlank
        private String stemcellName; //스템셀 명
        private String version; // 스템셀 버전
        private String directorId;
        public String getStemcellName() {
            return stemcellName;
        }
        public void setStemcellName(String stemcellName) {
            this.stemcellName = stemcellName;
        }
        public String getVersion() {
            return version;
        }
        public void setVersion(String version) {
            this.version = version;
        }
        public String getDirectorId() {
            return directorId;
        }
        public void setDirectorId(String directorId) {
            this.directorId = directorId;
        }
    }
}
