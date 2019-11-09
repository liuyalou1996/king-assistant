package com.universe.common.pojo;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ResponseDto {

  private String data;
  private String from;
  private Msg msg;
  private Integer status;

  public String getData() {
    return data;
  }

  public String getFrom() {
    return from;
  }

  public Msg getMsg() {
    return msg;
  }

  public Integer getStatus() {
    return status;
  }

  public void setData(String data) {
    this.data = data;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  public void setMsg(Msg msg) {
    this.msg = msg;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public static class Msg {

    private Integer page;
    private Integer pageSize;
    private List<Map<String, Object>> result;
    private Integer total;
    private Integer totalPage;

    public Integer getPage() {
      return page;
    }

    public Integer getPageSize() {
      return pageSize;
    }

    public List<Map<String, Object>> getResult() {
      return result;
    }

    public Integer getTotal() {
      return total;
    }

    public Integer getTotalPage() {
      return totalPage;
    }

    public void setPage(Integer page) {
      this.page = page;
    }

    public void setPageSize(Integer pageSize) {
      this.pageSize = pageSize;
    }

    public void setResult(List<Map<String, Object>> result) {
      this.result = result;
    }

    public void setTotal(Integer total) {
      this.total = total;
    }

    public void setTotalPage(Integer totalPage) {
      this.totalPage = totalPage;
    }

    @Override
    public String toString() {
      return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }

}
