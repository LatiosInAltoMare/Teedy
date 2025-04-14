package com.sismics.util;

import org.junit.Assert;
import org.junit.Test;
import com.sismics.docs.core.dao.dto.DocumentDto;

public class TestDocumentDto {

    @Test
    public void testGettersAndSetters() {
        // 创建一个 DocumentDto 实例
        DocumentDto dto = new DocumentDto();

        // 设置所有字段
        dto.setId("doc1");
        dto.setFileId("file1");
        dto.setTitle("Test Document");
        dto.setDescription("This is a test document");
        dto.setSubject("Test Subject");
        dto.setIdentifier("Test Identifier");
        dto.setPublisher("Test Publisher");
        dto.setFormat("PDF");
        dto.setSource("Test Source");
        dto.setType("Document");
        dto.setCoverage("Test Coverage");
        dto.setRights("All Rights Reserved");
        dto.setLanguage("English");
        dto.setCreateTimestamp(1625097600000L); // 2021-07-01
        dto.setUpdateTimestamp(1625184000000L); // 2021-07-02
        dto.setShared(true);
        dto.setFileCount(5);
        dto.setCreator("John Doe");
        dto.setActiveRoute(true);
        dto.setCurrentStepName("Step 1");
        dto.setHighlight("highlight text");

        // 验证所有 getter 方法
        Assert.assertEquals("doc1", dto.getId());
        Assert.assertEquals("file1", dto.getFileId());
        Assert.assertEquals("Test Document", dto.getTitle());
        Assert.assertEquals("This is a test document", dto.getDescription());
        Assert.assertEquals("Test Subject", dto.getSubject());
        Assert.assertEquals("Test Identifier", dto.getIdentifier());
        Assert.assertEquals("Test Publisher", dto.getPublisher());
        Assert.assertEquals("PDF", dto.getFormat());
        Assert.assertEquals("Test Source", dto.getSource());
        Assert.assertEquals("Document", dto.getType());
        Assert.assertEquals("Test Coverage", dto.getCoverage());
        Assert.assertEquals("All Rights Reserved", dto.getRights());
        Assert.assertEquals("English", dto.getLanguage());
        Assert.assertEquals(Long.valueOf(1625097600000L), dto.getCreateTimestamp());
        Assert.assertEquals(Long.valueOf(1625184000000L), dto.getUpdateTimestamp());
        Assert.assertTrue(dto.getShared());
        Assert.assertEquals(Integer.valueOf(5), dto.getFileCount());
        Assert.assertEquals("John Doe", dto.getCreator());
        Assert.assertTrue(dto.isActiveRoute());
        Assert.assertEquals("Step 1", dto.getCurrentStepName());
        Assert.assertEquals("highlight text", dto.getHighlight());
    }

    @Test
    public void testChainingMethods() {
        // 测试链式调用方法
        DocumentDto dto = new DocumentDto()
                .setFileId("file1")
                .setCurrentStepName("Step 1")
                .setHighlight("highlight text");

        // 验证链式调用后的值
        Assert.assertEquals("file1", dto.getFileId());
        Assert.assertEquals("Step 1", dto.getCurrentStepName());
        Assert.assertEquals("highlight text", dto.getHighlight());
    }

    @Test
    public void testBooleanFieldsWithDifferentValues() {
        // 测试 Boolean 字段 shared 和 boolean 字段 activeRoute 的不同值
        DocumentDto dto = new DocumentDto();

        // 测试 shared 为 true
        dto.setShared(true);
        Assert.assertTrue(dto.getShared());

        // 测试 shared 为 false
        dto.setShared(false);
        Assert.assertFalse(dto.getShared());

        // 测试 shared 为 null
        dto.setShared(null);
        Assert.assertNull(dto.getShared());

        // 测试 activeRoute 为 true
        dto.setActiveRoute(true);
        Assert.assertTrue(dto.isActiveRoute());

        // 测试 activeRoute 为 false
        dto.setActiveRoute(false);
        Assert.assertFalse(dto.isActiveRoute());
    }

    @Test
    public void testNullValues() {
        // 测试所有字段设置为 null 的情况（对于可为 null 的字段）
        DocumentDto dto = new DocumentDto();

        // 设置所有字段为 null（仅适用于非基本类型字段）
        dto.setId(null);
        dto.setFileId(null);
        dto.setTitle(null);
        dto.setDescription(null);
        dto.setSubject(null);
        dto.setIdentifier(null);
        dto.setPublisher(null);
        dto.setFormat(null);
        dto.setSource(null);
        dto.setType(null);
        dto.setCoverage(null);
        dto.setRights(null);
        dto.setLanguage(null);
        dto.setCreateTimestamp(null);
        dto.setUpdateTimestamp(null);
        dto.setShared(null);
        dto.setFileCount(null);
        dto.setCreator(null);
        dto.setCurrentStepName(null);
        dto.setHighlight(null);

        // 验证所有 getter 方法返回 null
        Assert.assertNull(dto.getId());
        Assert.assertNull(dto.getFileId());
        Assert.assertNull(dto.getTitle());
        Assert.assertNull(dto.getDescription());
        Assert.assertNull(dto.getSubject());
        Assert.assertNull(dto.getIdentifier());
        Assert.assertNull(dto.getPublisher());
        Assert.assertNull(dto.getFormat());
        Assert.assertNull(dto.getSource());
        Assert.assertNull(dto.getType());
        Assert.assertNull(dto.getCoverage());
        Assert.assertNull(dto.getRights());
        Assert.assertNull(dto.getLanguage());
        Assert.assertNull(dto.getCreateTimestamp());
        Assert.assertNull(dto.getUpdateTimestamp());
        Assert.assertNull(dto.getShared());
        Assert.assertNull(dto.getFileCount());
        Assert.assertNull(dto.getCreator());
        Assert.assertNull(dto.getCurrentStepName());
        Assert.assertNull(dto.getHighlight());

        // activeRoute 是基本类型 boolean，无法为 null，测试默认值
        Assert.assertFalse(dto.isActiveRoute());
    }
}