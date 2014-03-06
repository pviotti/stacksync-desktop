package com.stacksync.desktop.db.models;

import com.stacksync.commons.models.Workspace;
import com.stacksync.desktop.db.PersistentObject;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.OneToMany;

@Entity
@IdClass(value = CloneWorkspacePk.class)
public class CloneWorkspace extends PersistentObject implements Serializable {
        
    private static final long serialVersionUID = 3232299912L;

    @Id
    @Column(name="id", nullable=false)
    private String id;
    
    @Column(name="path_workspace", nullable=false)
    private String pathWorkspace;
    
    @Column(name="local_revision", nullable=false)
    private Integer localLastUpdate;
    
    @Column(name="remote_revision", nullable=false)
    private Integer remoteLastUpdate;
    
    @Column(name="swift_container")
    private String swiftContainer;
    
    @Column(name="swift_url")
    private String swiftStorageURL;
    
    @Column(name="owner", nullable=false)
    private String owner;
    
    @Column(name="parent_id")
    private Long parentId;
    
    @Column(name="name", nullable=false)
    private String name;
          
    @OneToMany
    private List<CloneFile> files;
    
    public CloneWorkspace(){}
    
    public CloneWorkspace(Workspace r){
        this.id = r.getId().toString();

        this.pathWorkspace = "/"+r.getName();
        if (r.getName().equals("default")) {
            this.pathWorkspace = "/";
        }
        
        this.name = r.getName();
        
        this.parentId = null;
        if (r.getParentItem() != null) {
            this.parentId = r.getParentItem().getId();
        }
        
        this.localLastUpdate = r.getLatestRevision();
        this.remoteLastUpdate = r.getLatestRevision();
        this.swiftContainer = r.getSwiftContainer();
        this.swiftStorageURL = r.getSwiftUrl();
        this.owner = r.getOwner().getId().toString();
    }

    public String getId() {
        return id;
    }
    
    public String getPathWorkspace(){
        return pathWorkspace;
    }
    
    public void setPathWorkspace(String pathWorkspace){
        this.pathWorkspace = pathWorkspace;
    }

    public Integer getLocalLastUpdate() {
        return localLastUpdate;
    }
    
    public void setLocalLastUpdate(Integer localLastUpdate) {
        this.localLastUpdate = localLastUpdate;
    }
    
    public Integer getRemoteRevision() {
        return remoteLastUpdate;
    }
    
    public void setRemoteRevision(Integer remoteLastUpdate) {
        this.remoteLastUpdate = remoteLastUpdate;
    }    

    public String getSwiftContainer() {
        return swiftContainer;
    }

    public void setSwiftContainer(String swiftContainer) {
        this.swiftContainer = swiftContainer;
    }

    public String getSwiftStorageURL() {
        return swiftStorageURL;
    }

    public void setSwiftStorageURL(String swiftStorageURL) {
        this.swiftStorageURL = swiftStorageURL;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof CloneWorkspace)) {
            return false;
        }
        
        CloneWorkspace other = (CloneWorkspace) object;
        return this.id.compareTo(other.id) == 0;        
    }

    @Override
    public String toString() {
        return "CloneWorkspace[id=" + id + ", pathWorkspace=" + pathWorkspace + ", localLastUpdate=" + localLastUpdate + ", remoteLastUpdate=" + remoteLastUpdate + "]";
    }
        
    public List<CloneFile> getFiles(){
        return this.files;
    }
    
}