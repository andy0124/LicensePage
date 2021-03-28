package License.login.request_response;

public class QueryParams {
    private String type;
    private Long id;
    private Long projectId;
    private Long projectName;
    private String filters;
    private String select;
    private String sortField;
    private String sortDir;
    private Integer limit;
    private Boolean upsert;
    private Boolean overwrite;
    private Integer start;
    private String page;
    private String key;
    private String value;
    private String valueType;
    private Integer numFold;
    private Boolean bTraining;
    private String userId;
    private String password;
    private String newPassword;
    private String authToken;
    private String userQuery;
    private Boolean drop;
    private String filterStr;
    private String filterJson;
    private String apiKey;


    public KnowledgeRequestType getType() {
        return KnowledgeRequestType.fromText(type);
    }

    public void setType(KnowledgeRequestType type) {
        this.type = type.getValue();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }


    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getSelect() {
        return select;
    }

    public void setSelect(String selects) {
        this.select = selects;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public String getSortDir() {
        return sortDir;
    }

    public void setSortDir(String sortDir) {
        this.sortDir = sortDir;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getFilters() {
        return filters;
    }

    public void setFilters(String filters) {
        this.filters = filters;
    }

    public Boolean getUpsert() {
        return upsert;
    }

    public void setUpsert(Boolean upsert) {
        this.upsert = upsert;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public Boolean getbTraining() {
        return bTraining;
    }

    public void setbTraining(Boolean bTraining) {
        this.bTraining = bTraining;
    }

    public Integer getNumFold() {
        return numFold;
    }

    public void setNumFold(Integer numFold) {
        this.numFold = numFold;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getUserQuery() {
        return userQuery;
    }

    public void setUserQuery(String userQuery) {
        this.userQuery = userQuery;
    }

    public Boolean getOverwrite() {
        return overwrite;
    }

    public void setOverwrite(Boolean overwrite) {
        this.overwrite = overwrite;
    }

    public Boolean getDrop(){
        return drop;
    }
    public void setDrop(Boolean drop){
        this.drop = drop;
    }

    public String getFilterStr() {
        return filterStr;
    }

    public void setFilterStr(String filterStr) {
        this.filterStr = filterStr;
    }

    public String getFilterJson() {
        return filterJson;
    }

    public void setFilterJson(String filterJson) {
        this.filterJson = filterJson;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("QueryParams{");
        sb.append("type='").append(type).append('\'');
        sb.append(", id=").append(id);
        sb.append(", projectId=").append(projectId);
        sb.append(", filters='").append(filters).append('\'');
        sb.append(", select='").append(select).append('\'');
        sb.append(", sortField='").append(sortField).append('\'');
        sb.append(", sortDir='").append(sortDir).append('\'');
        sb.append(", limit=").append(limit);
        sb.append(", upsert=").append(upsert);
        sb.append(", overwrite=").append(overwrite);
        sb.append(", start=").append(start);
        sb.append(", page='").append(page).append('\'');
        sb.append(", key='").append(key).append('\'');
        sb.append(", value='").append(value).append('\'');
        sb.append(", valueType='").append(valueType).append('\'');
        sb.append(", numFold=").append(numFold);
        sb.append(", bTraining=").append(bTraining);
        sb.append(", userId='").append(userId).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append(", newPassword='").append(newPassword).append('\'');
        sb.append(", authToken='").append(authToken).append('\'');
        sb.append(", userQuery='").append(userQuery).append('\'');
        sb.append(", drop=").append(drop);
        sb.append('}');
        return sb.toString();
    }
}
