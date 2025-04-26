package com.striveonger.common.open.apis.web.controller;

import com.striveonger.common.core.constant.ResultStatus;
import com.striveonger.common.core.result.Result;
import com.striveonger.common.open.apis.storage.LRUStorage;
import com.striveonger.common.open.apis.web.dto.StorageItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Mr.Lee
 * @since 2025-04-26 10:18
 */
@RestController
public class MemoryStorageController {
    private final Logger log = LoggerFactory.getLogger(MemoryStorageController.class);

    private final LRUStorage<StorageItem> storage;

    public MemoryStorageController(@Value("${own.open-apis.storage.memory.max-rows:1000}") Integer maxRows) {
        this.storage = new LRUStorage<>(maxRows);
    }

    @PostMapping("/storage/{key}")
    public Result save(@PathVariable String key, @RequestBody Map<String, String> data) {
        log.info("save key={}, data={}", key, data);
        String value = data.get("value");
        String description = data.get("description");
        if (storage.containsKey(key)) {
            return Result.fail().message("key already exists");
        }
        StorageItem item = new StorageItem();
        item.setKey(key);
        item.setValue(value);
        item.setDescription(description);
        item.setCreateTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        storage.put(key, item);
        return Result.success();
    }

    @DeleteMapping("/storage/{key}")
    public Result delete(@PathVariable String key) {
        log.info("delete key={}", key);

        StorageItem item = storage.remove(key);
        if (Objects.nonNull(item)) {
            return Result.success();
        } else {
            return Result.status(ResultStatus.NOT_FOUND);
        }
    }

    @PutMapping("/storage/{key}")
    public Result update(@PathVariable String key, @RequestBody Map<String, String> data) {
        log.info("update key={}, data={}", key, data);
        String value = data.get("value");
        String description = data.get("description");
        if (storage.containsKey(key)) {
            StorageItem item = storage.get(key);
            item.setValue(value);
            item.setDescription(description);
            item.setUpdateTime(LocalDateTime.now());
            storage.put(key, item);
            return Result.success();
        }
        return Result.status(ResultStatus.NOT_FOUND);
    }

    @GetMapping("/storage/all")
    public Result list() {
        log.info("list");
        List<StorageItem> list = storage.values().stream().sorted(Comparator.comparing(StorageItem::getUpdateTime)).toList();
        return Result.success().data(list);
    }

    @GetMapping("/storage/{key}")
    public Result get(@PathVariable String key) {
        log.info("get key={}", key);
        if (Objects.isNull(key)) {
            return Result.fail().message("key is null");
        }
        StorageItem item = storage.get(key);
        if (Objects.nonNull(item)) {
            return Result.success().data(item);
        } else {
            return Result.status(ResultStatus.NOT_FOUND);
        }
    }
}
