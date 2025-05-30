package com.mojian.controller.comment;


import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mojian.service.CommentService;
import com.mojian.vo.comment.CommentListVo;
import com.mojian.common.Result;
import com.mojian.entity.SysComment;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
@Api(tags = "门户-评论管理")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/list")
    @ApiOperation(value = "获取文章评论列表")
    public Result<IPage<CommentListVo>> getComments(Integer articleId,String sortType) {
        return Result.success(commentService.getComments(articleId,sortType));
    }

    @SaCheckLogin
    @PostMapping("/add")
    @ApiOperation(value = "获取文章评论列表")
    public Result<Void> add(@RequestBody SysComment sysComment) {
        commentService.add(sysComment);
        return Result.success();
    }
}
