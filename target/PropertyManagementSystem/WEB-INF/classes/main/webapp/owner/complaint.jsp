<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>投诉建议 - 智慧社区</title>
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/4.6.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
            font-family: 'Microsoft YaHei', 'Segoe UI', sans-serif;
            min-height: 100vh;
        }

        /* 🔥 页面头部 */
        .page-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 50px 0 30px;
            margin-bottom: 40px;
            box-shadow: 0 8px 32px rgba(102, 126, 234, 0.3);
            position: relative;
            overflow: hidden;
        }

        .page-header::before {
            content: '';
            position: absolute;
            top: -50%;
            right: -10%;
            width: 500px;
            height: 500px;
            background: rgba(255,255,255,0.1);
            border-radius: 50%;
        }

        .page-header h2 {
            font-size: 32px;
            font-weight: 700;
            margin-bottom: 10px;
            position: relative;
            z-index: 1;
        }

        .page-header p {
            font-size: 16px;
            opacity: 0.9;
            position: relative;
            z-index: 1;
        }

        /* 🔥 卡片容器 */
        .card-box {
            background: white;
            border-radius: 16px;
            border: none;
            box-shadow: 0 4px 20px rgba(0,0,0,0.08);
            margin-bottom: 30px;
            overflow: hidden;
            transition: all 0.3s;
        }

        .card-box:hover {
            box-shadow: 0 8px 32px rgba(0,0,0,0.12);
            transform: translateY(-2px);
        }

        /* 🔥 投诉卡片 */
        .complaint-card {
            border: 2px solid #f0f0f0;
            border-radius: 12px;
            padding: 20px;
            margin-bottom: 20px;
            background: white;
            transition: all 0.3s;
            cursor: pointer;
            position: relative;
            overflow: hidden;
        }

        .complaint-card::before {
            content: '';
            position: absolute;
            left: 0;
            top: 0;
            width: 4px;
            height: 100%;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            transform: scaleY(0);
            transition: transform 0.3s;
        }

        .complaint-card:hover {
            border-color: #667eea;
            box-shadow: 0 8px 24px rgba(102, 126, 234, 0.15);
            transform: translateX(4px);
        }

        .complaint-card:hover::before {
            transform: scaleY(1);
        }

        .complaint-card.cancelled {
            background: #fafafa;
            border-color: #e0e0e0;
            opacity: 0.8;
        }

        .complaint-header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 12px;
        }

        .complaint-title {
            font-size: 16px;
            font-weight: 700;
            color: #2c3e50;
            flex: 1;
            margin-right: 15px;
        }

        .complaint-preview {
            font-size: 14px;
            color: #7f8c8d;
            line-height: 1.8;
            margin-bottom: 15px;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
        }

        .complaint-footer {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding-top: 12px;
            border-top: 1px solid #f0f0f0;
        }

        .complaint-meta {
            display: flex;
            align-items: center;
            gap: 12px;
            flex-wrap: wrap;
        }

        /* 🔥 徽章样式 */
        .type-badge {
            padding: 4px 10px;
            border-radius: 20px;
            font-size: 11px;
            font-weight: 600;
            display: inline-flex;
            align-items: center;
            gap: 4px;
        }

        .type-service {
            background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
            color: #1976d2;
            border: 1px solid #90caf9;
        }
        .type-environment {
            background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%);
            color: #388e3c;
            border: 1px solid #81c784;
        }
        .type-facility {
            background: linear-gradient(135deg, #fff3e0 0%, #ffe0b2 100%);
            color: #f57c00;
            border: 1px solid #ffb74d;
        }
        .type-fee {
            background: linear-gradient(135deg, #fce4ec 0%, #f8bbd0 100%);
            color: #c2185b;
            border: 1px solid #f06292;
        }
        .type-other {
            background: linear-gradient(135deg, #f3e5f5 0%, #e1bee7 100%);
            color: #7b1fa2;
            border: 1px solid #ba68c8;
        }

        .status-badge {
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 11px;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        .status-pending {
            background: linear-gradient(135deg, #fff3e0 0%, #ffe0b2 100%);
            color: #f57c00;
            border: 2px solid #ffb74d;
        }
        .status-processing {
            background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
            color: #1976d2;
            border: 2px solid #64b5f6;
        }
        .status-resolved {
            background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%);
            color: #388e3c;
            border: 2px solid #66bb6a;
        }
        .status-closed {
            background: linear-gradient(135deg, #f5f5f5 0%, #e0e0e0 100%);
            color: #757575;
            border: 2px solid #bdbdbd;
        }
        .status-cancelled {
            background: linear-gradient(135deg, #ffebee 0%, #ffcdd2 100%);
            color: #c62828;
            border: 2px solid #ef5350;
        }

        /* 🔥 筛选框（完整显示版） */
        .filter-box {
            background: white;
            padding: 25px;
            border-radius: 12px;
            margin-bottom: 25px;
            box-shadow: 0 2px 12px rgba(0,0,0,0.06);
        }

        /* 搜索框 */
        .search-input-wrapper {
            margin-bottom: 20px;
        }

        .search-input-wrapper .input-group {
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
            border-radius: 8px;
            overflow: hidden;
        }

        .search-input-wrapper .input-group-prepend {
            flex-shrink: 0;
        }

        .search-input-wrapper .input-group-text {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            color: white;
            padding: 12px 16px;
            font-size: 16px;
        }

        .search-input-wrapper .form-control {
            border: 2px solid #e9ecef;
            border-left: none;
            padding: 12px 16px;
            font-size: 14px;
            height: auto;
        }

        .search-input-wrapper .form-control:focus {
            outline: none;
            border-color: #667eea;
            box-shadow: none;
        }

        /* 下拉框容器 */
        .filter-selects {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 15px;
            margin-bottom: 20px;
        }

        .filter-select-wrapper {
            position: relative;
        }

        .filter-select-wrapper label {
            font-size: 13px;
            font-weight: 600;
            color: #6c757d;
            margin-bottom: 8px;
            display: flex;
            align-items: center;
            gap: 6px;
        }

        .filter-select-wrapper label i {
            color: #667eea;
            font-size: 14px;
        }

        .filter-box select.form-control {
            border: 2px solid #e9ecef;
            border-radius: 8px;
            padding: 11px 40px 11px 14px;
            font-size: 14px;
            height: auto;
            line-height: 1.5;
            background-color: white;
            background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 12 12'%3E%3Cpath fill='%23667eea' d='M6 9L1 4h10z'/%3E%3C/svg%3E");
            background-repeat: no-repeat;
            background-position: right 14px center;
            background-size: 12px;
            appearance: none;
            -webkit-appearance: none;
            -moz-appearance: none;
            cursor: pointer;
            transition: all 0.3s;
            white-space: normal;
            word-wrap: break-word;
        }

        .filter-box select.form-control option {
            padding: 10px;
            font-size: 14px;
            line-height: 1.6;
        }

        .filter-box select.form-control:hover {
            border-color: #667eea;
            box-shadow: 0 2px 8px rgba(102, 126, 234, 0.15);
        }

        .filter-box select.form-control:focus {
            outline: none;
            border-color: #667eea;
            box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.15);
        }

        /* 按钮组 */
        .filter-actions {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 12px;
        }

        .filter-actions .btn {
            border-radius: 8px;
            font-weight: 600;
            padding: 13px 20px;
            transition: all 0.3s;
            border: none;
            font-size: 15px;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
            height: 48px;
            white-space: nowrap;
        }

        .filter-actions .btn i {
            font-size: 15px;
        }

        .filter-actions .btn-search {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
        }

        .filter-actions .btn-search:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
        }

        .filter-actions .btn-reset {
            background: white;
            color: #6c757d;
            border: 2px solid #dee2e6;
        }

        .filter-actions .btn-reset:hover {
            background: #f8f9fa;
            border-color: #adb5bd;
            color: #495057;
            transform: translateY(-2px);
        }

        /* 🔥 详情弹窗 */
        .detail-modal .modal-dialog {
            max-width: 900px;
        }

        .detail-modal .modal-content {
            border-radius: 20px;
            border: none;
            overflow: hidden;
            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
        }

        .detail-modal .modal-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 25px 30px;
            border: none;
            position: relative;
        }

        .detail-modal .modal-header::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 3px;
            background: linear-gradient(90deg, #ffd700, #ff6b6b, #4ecdc4, #45b7d1);
        }

        .detail-modal .modal-title {
            font-size: 20px;
            font-weight: 700;
            display: flex;
            align-items: center;
            gap: 12px;
        }

        .detail-modal .close {
            color: white;
            opacity: 1;
            text-shadow: none;
            font-size: 32px;
            font-weight: 300;
        }

        .detail-modal .modal-body {
            padding: 30px;
            max-height: 75vh;
            overflow-y: auto;
            background: #fafafa;
        }

        /* 🔥 详情头部 */
        .detail-header-info {
            background: white;
            padding: 25px;
            border-radius: 12px;
            margin-bottom: 25px;
            box-shadow: 0 2px 12px rgba(0,0,0,0.06);
        }

        .detail-title {
            font-size: 24px;
            font-weight: 700;
            color: #2c3e50;
            margin-bottom: 15px;
            line-height: 1.4;
        }

        .detail-badges {
            display: flex;
            gap: 10px;
            flex-wrap: wrap;
        }

        /* 🔥 内容卡片 */
        .content-section {
            background: white;
            padding: 25px;
            border-radius: 12px;
            margin-bottom: 20px;
            box-shadow: 0 2px 12px rgba(0,0,0,0.06);
        }

        .section-title {
            font-size: 14px;
            font-weight: 700;
            color: #667eea;
            margin-bottom: 15px;
            display: flex;
            align-items: center;
            gap: 8px;
            text-transform: uppercase;
            letter-spacing: 1px;
        }

        .section-title i {
            font-size: 16px;
        }

        .content-text {
            font-size: 15px;
            color: #34495e;
            line-height: 1.8;
            white-space: pre-wrap;
            word-wrap: break-word;
            padding: 15px;
            background: #f8f9fa;
            border-radius: 8px;
            border-left: 4px solid #667eea;
        }

        /* 🔥 追加时间线 */
        .append-timeline {
            background: white;
            padding: 25px;
            border-radius: 12px;
            margin-bottom: 20px;
            box-shadow: 0 2px 12px rgba(0,0,0,0.06);
        }

        .timeline-header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 25px;
            padding-bottom: 15px;
            border-bottom: 2px solid #f0f0f0;
        }

        .timeline-title {
            font-size: 16px;
            font-weight: 700;
            color: #667eea;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .timeline-count {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 700;
        }

        .timeline-item {
            position: relative;
            padding-left: 45px;
            padding-bottom: 30px;
        }

        .timeline-item:last-child {
            padding-bottom: 0;
        }

        .timeline-item::before {
            content: '';
            position: absolute;
            left: 14px;
            top: 30px;
            bottom: 0;
            width: 2px;
            background: linear-gradient(to bottom, #667eea, transparent);
        }

        .timeline-item:last-child::before {
            display: none;
        }

        .timeline-dot {
            position: absolute;
            left: 0;
            top: 0;
            width: 30px;
            height: 30px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 14px;
            box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
            z-index: 1;
        }

        .timeline-content {
            background: #f8f9fa;
            padding: 18px;
            border-radius: 10px;
            border: 1px solid #e9ecef;
            transition: all 0.3s;
        }

        .timeline-content:hover {
            background: white;
            box-shadow: 0 4px 12px rgba(0,0,0,0.08);
            transform: translateX(4px);
        }

        .timeline-time {
            font-size: 12px;
            color: #999;
            margin-bottom: 10px;
            display: flex;
            align-items: center;
            gap: 6px;
        }

        .timeline-text {
            font-size: 14px;
            color: #2c3e50;
            line-height: 1.8;
            white-space: pre-wrap;
            word-wrap: break-word;
        }

        /* 🔥 物业回复 */
        .reply-section {
            background: white;
            padding: 25px;
            border-radius: 12px;
            box-shadow: 0 2px 12px rgba(0,0,0,0.06);
            border: 2px solid #ffa726;
            position: relative;
            overflow: hidden;
        }

        .reply-section::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            width: 5px;
            height: 100%;
            background: linear-gradient(to bottom, #ffa726, #ff9800);
        }

        /* 🔥 业主撤销样式 */
        .reply-section.owner-cancelled {
            border-color: #95a5a6;
        }

        .reply-section.owner-cancelled::before {
            background: linear-gradient(to bottom, #95a5a6, #7f8c8d);
        }

        .reply-section.owner-cancelled .reply-avatar {
            background: linear-gradient(135deg, #95a5a6 0%, #7f8c8d 100%);
            box-shadow: 0 4px 12px rgba(149, 165, 166, 0.4);
        }

        .reply-section.owner-cancelled .reply-author {
            color: #7f8c8d;
        }

        .reply-section.owner-cancelled .reply-content {
            background: #ecf0f1;
            border-left: 4px solid #95a5a6;
        }

        /* 🔥 管理员驳回样式 */
        .reply-section.rejected {
            border-color: #e74c3c;
        }

        .reply-section.rejected::before {
            background: linear-gradient(to bottom, #e74c3c, #c0392b);
        }

        .reply-section.rejected .reply-avatar {
            background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%);
            box-shadow: 0 4px 12px rgba(231, 76, 60, 0.4);
        }

        .reply-section.rejected .reply-author {
            color: #c0392b;
        }

        .reply-section.rejected .reply-content {
            background: #ffebee;
            border-left: 4px solid #e74c3c;
        }

        .reply-header {
            display: flex;
            align-items: center;
            gap: 15px;
            margin-bottom: 20px;
            padding-bottom: 15px;
            border-bottom: 2px solid #f0f0f0;
        }

        .reply-avatar {
            width: 50px;
            height: 50px;
            background: linear-gradient(135deg, #ffa726 0%, #ff9800 100%);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 24px;
            box-shadow: 0 4px 12px rgba(255, 167, 38, 0.4);
        }

        .reply-info {
            flex: 1;
        }

        .reply-author {
            font-size: 16px;
            font-weight: 700;
            color: #f57c00;
            margin-bottom: 4px;
        }

        .reply-time {
            font-size: 12px;
            color: #999;
            display: flex;
            align-items: center;
            gap: 6px;
        }

        .reply-content {
            font-size: 15px;
            color: #2c3e50;
            line-height: 1.8;
            padding: 15px;
            background: #fff8e1;
            border-radius: 8px;
            white-space: pre-wrap;
            word-wrap: break-word;
        }

        /* 🔥 元信息 */
        .meta-info {
            background: white;
            padding: 20px;
            border-radius: 12px;
            margin-bottom: 20px;
            box-shadow: 0 2px 12px rgba(0,0,0,0.06);
        }

        .meta-item {
            display: flex;
            align-items: center;
            padding: 12px 0;
            border-bottom: 1px solid #f0f0f0;
        }

        .meta-item:last-child {
            border-bottom: none;
        }

        .meta-label {
            font-size: 13px;
            color: #999;
            width: 100px;
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .meta-value {
            font-size: 14px;
            color: #2c3e50;
            font-weight: 500;
        }

        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            border-radius: 8px;
            padding: 10px 20px;
            font-weight: 600;
            box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
            font-size: 14px;
        }

        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
        }

        /* 🔥 分页 */
        #pagination {
            margin-top: 30px;
            padding-top: 20px;
            border-top: 2px solid #f0f0f0;
        }

        #pagination .pagination {
            margin-bottom: 10px;
        }

        #pagination .page-link {
            border-radius: 6px;
            margin: 0 4px;
            color: #667eea;
            border: 2px solid #e9ecef;
            transition: all 0.3s;
            font-weight: 600;
            padding: 6px 12px;
            font-size: 13px;
        }

        #pagination .page-link:hover {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border-color: #667eea;
            transform: translateY(-2px);
        }

        #pagination .page-item.disabled .page-link {
            background: #f8f9fa;
            border-color: #dee2e6;
            color: #6c757d;
        }

        /* 🔥 空状态 */
        .empty-state {
            text-align: center;
            padding: 80px 20px;
            color: #999;
        }

        .empty-state i {
            font-size: 80px;
            margin-bottom: 20px;
            opacity: 0.3;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }

        /* 🔥 字符计数 */
        .char-count {
            font-size: 12px;
            color: #999;
            text-align: right;
            margin-top: 5px;
        }

        .char-count.danger { color: #e74c3c; font-weight: 700; }

        /* 🔥 返回顶部 */
        .back-to-top {
            position: fixed;
            bottom: 40px;
            right: 40px;
            width: 56px;
            height: 56px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            border-radius: 50%;
            box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
            cursor: pointer;
            display: none;
            z-index: 1000;
            transition: all 0.3s;
        }

        .back-to-top:hover {
            transform: translateY(-5px) scale(1.1);
            box-shadow: 0 8px 28px rgba(102, 126, 234, 0.5);
        }

        /* 🔥 滚动条 */
        .modal-body::-webkit-scrollbar {
            width: 8px;
        }

        .modal-body::-webkit-scrollbar-track {
            background: #f1f1f1;
            border-radius: 10px;
        }

        .modal-body::-webkit-scrollbar-thumb {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-radius: 10px;
        }

        /* 🔥 响应式 */
        @media (max-width: 768px) {
            .page-header { padding: 40px 0 20px; }
            .page-header h2 { font-size: 24px; }
            .detail-modal .modal-dialog { max-width: 95%; margin: 10px auto; }
            .filter-selects {
                grid-template-columns: 1fr;
            }
            .filter-actions {
                grid-template-columns: 1fr;
            }
            .timeline-item { padding-left: 35px; }
            .timeline-dot { width: 24px; height: 24px; font-size: 12px; }
        }
    </style>
</head>
<body>

<div class="page-header">
    <div class="container">
        <h2><i class="fas fa-envelope-open-text mr-2"></i>投诉建议</h2>
        <p class="mb-0">您的声音我们倾听,共建美好社区</p>
    </div>
</div>

<div class="container">
    <div class="row">
        <!-- 左侧：提交表单 -->
        <div class="col-lg-4">
            <div class="card-box p-4">
                <h5 class="font-weight-bold mb-4">
                    <i class="fas fa-edit text-primary"></i> 我要反馈
                </h5>
                <form id="complaintForm">
                    <div class="form-group">
                        <label><i class="fas fa-tag"></i> 反馈类型 <span class="text-danger">*</span></label>
                        <select class="form-control" id="complaintType" required>
                            <option value="">请选择类型</option>
                            <option value="service">👮 保安/服务态度</option>
                            <option value="environment">🧹 卫生/绿化环境</option>
                            <option value="facility">💡 设施设备问题</option>
                            <option value="fee">💰 费用疑问</option>
                            <option value="other">📝 其他建议</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label><i class="fas fa-heading"></i> 标题 <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="title" placeholder="简要描述主题" maxlength="100" required>
                        <div class="char-count" id="titleCount">0/100</div>
                    </div>

                    <div class="form-group">
                        <label><i class="fas fa-align-left"></i> 详细内容 <span class="text-danger">*</span></label>
                        <textarea class="form-control" id="content" rows="5" placeholder="请详细描述您遇到的问题或建议..." maxlength="500" required></textarea>
                        <div class="char-count" id="contentCount">0/500</div>
                    </div>

                    <div class="form-group">
                        <div class="custom-control custom-checkbox">
                            <input type="checkbox" class="custom-control-input" id="isAnonymous">
                            <label class="custom-control-label" for="isAnonymous">
                                <i class="fas fa-user-secret"></i> 匿名提交
                            </label>
                        </div>
                        <small class="text-muted">勾选后,物业将无法看到您的姓名和联系方式</small>
                    </div>

                    <button type="button" class="btn btn-primary btn-block font-weight-bold" onclick="submitComplaint()">
                        <i class="fas fa-paper-plane"></i> 提交反馈
                    </button>
                </form>
            </div>
        </div>

        <!-- 右侧：历史记录 -->
        <div class="col-lg-8">
            <div class="card-box p-4">
                <h5 class="font-weight-bold mb-4">
                    <i class="fas fa-list-alt text-primary"></i> 我的反馈记录
                </h5>

                <!-- 筛选器 -->
                <div class="filter-box">
                    <!-- 搜索框 -->
                    <div class="search-input-wrapper">
                        <div class="input-group">
                            <div class="input-group-prepend">
                                <span class="input-group-text">
                                    <i class="fas fa-search"></i>
                                </span>
                            </div>
                            <input type="text"
                                   class="form-control"
                                   id="searchKeyword"
                                   placeholder="搜索标题或内容关键词..."
                                   onkeypress="if(event.keyCode==13) { event.preventDefault(); loadComplaints(1); }">
                        </div>
                    </div>

                    <!-- 下拉筛选 -->
                    <div class="filter-selects">
                        <div class="filter-select-wrapper">
                            <label><i class="fas fa-tag"></i> 反馈类型</label>
                            <select class="form-control" id="filterType">
                                <option value="">全部类型</option>
                                <option value="service">👮 保安/服务态度</option>
                                <option value="environment">🧹 卫生/绿化环境</option>
                                <option value="facility">💡 设施设备问题</option>
                                <option value="fee">💰 费用疑问</option>
                                <option value="other">📝 其他建议</option>
                            </select>
                        </div>

                        <div class="filter-select-wrapper">
                            <label><i class="fas fa-flag"></i> 处理状态</label>
                            <select class="form-control" id="filterStatus">
                                <option value="">全部状态</option>
                                <option value="pending">⏳ 待处理</option>
                                <option value="processing">🔄 处理中</option>
                                <option value="resolved">✅ 已解决</option>
                                <option value="closed">🔒 已关闭</option>
                            </select>
                        </div>
                    </div>

                    <!-- 操作按钮 -->
                    <div class="filter-actions">
                        <button class="btn btn-search" onclick="loadComplaints(1)">
                            <i class="fas fa-search"></i>
                            <span>搜索</span>
                        </button>
                        <button class="btn btn-reset" onclick="resetSearch()">
                            <i class="fas fa-redo"></i>
                            <span>重置</span>
                        </button>
                    </div>
                </div>

                <!-- 投诉列表 -->
                <div id="complaintList">
                    <div class="text-center py-5">
                        <i class="fas fa-spinner fa-spin fa-2x text-muted"></i>
                        <p class="text-muted mt-3">加载中...</p>
                    </div>
                </div>

                <!-- 分页 -->
                <nav id="pagination" class="mt-3"></nav>
            </div>
        </div>
    </div>
</div>

<!-- 返回顶部按钮 -->
<button class="back-to-top" id="backToTop" onclick="scrollToTop()">
    <i class="fas fa-arrow-up"></i>
</button>

<!-- 🔥 详情弹窗 -->
<div class="modal fade detail-modal" id="detailModal" tabindex="-1" role="dialog">
    <div class="modal-dialog modal-dialog-centered" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">
                    <i class="fas fa-file-alt"></i>投诉详情
                </h5>
                <button type="button" class="close" data-dismiss="modal">
                    <span>&times;</span>
                </button>
            </div>
            <div class="modal-body" id="detailContent">
                <!-- 详情内容将通过 JS 动态填充 -->
            </div>
            <div class="modal-footer" id="detailActions">
                <!-- 操作按钮将通过 JS 动态填充 -->
            </div>
        </div>
    </div>
</div>

<!-- 追加说明 Modal -->
<div class="modal fade" id="appendModal" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content" style="border-radius: 16px; overflow: hidden;">
            <div class="modal-header" style="background: linear-gradient(135deg, #17a2b8 0%, #138496 100%); color: white; border: none;">
                <h5 class="modal-title" style="font-weight: 700;"><i class="fas fa-plus-circle mr-2"></i>追加说明</h5>
                <button type="button" class="close text-white" data-dismiss="modal" style="opacity: 1;">
                    <span>&times;</span>
                </button>
            </div>
            <div class="modal-body" style="padding: 30px;">
                <input type="hidden" id="appendComplaintId">
                <div class="form-group">
                    <label style="font-weight: 600; color: #2c3e50;">补充内容：</label>
                    <textarea class="form-control" id="appendContent" rows="5" placeholder="请输入需要补充的情况..." maxlength="300" style="border: 2px solid #e9ecef; border-radius: 8px;"></textarea>
                    <small class="text-muted">最多300字</small>
                </div>
            </div>
            <div class="modal-footer" style="border-top: 2px solid #f0f0f0;">
                <button type="button" class="btn btn-secondary" data-dismiss="modal" style="border-radius: 8px; padding: 8px 20px; font-size: 13px;">
                    <i class="fas fa-times"></i> 取消
                </button>
                <button type="button" class="btn btn-info" onclick="submitAppend()" style="border-radius: 8px; padding: 8px 20px; font-weight: 600; font-size: 13px;">
                    <i class="fas fa-check"></i> 确认提交
                </button>
            </div>
        </div>
    </div>

</div>

<!-- 撤销投诉 Modal -->
<div class="modal fade" id="cancelModal" tabindex="-1" role="dialog">
    <div class="modal-dialog modal-dialog-centered" role="document">
        <div class="modal-content" style="border-radius: 16px; overflow: hidden;">
            <div class="modal-header" style="background: linear-gradient(135deg, #f39c12 0%, #e67e22 100%); color: white; border: none;">
                <h5 class="modal-title" style="font-weight: 700;">
                    <i class="fas fa-undo mr-2"></i>撤销投诉
                </h5>
                <button type="button" class="close text-white" data-dismiss="modal" style="opacity: 1;">
                    <span>&times;</span>
                </button>
            </div>
            <div class="modal-body" style="padding: 30px;">
                <input type="hidden" id="cancelComplaintId">

                <div class="alert alert-warning" style="border-radius: 8px; border-left: 4px solid #f39c12;">
                    <i class="fas fa-exclamation-triangle mr-2"></i>
                    <strong>提示：</strong>撤销后该投诉将被关闭，无法继续处理
                </div>

                <div class="form-group">
                    <label style="font-weight: 600; color: #2c3e50;">
                        <i class="fas fa-edit mr-1"></i>撤销原因：
                        <span class="text-danger">*</span>
                    </label>
                    <textarea
                            class="form-control"
                            id="cancelReason"
                            rows="5"
                            placeholder="请详细说明撤销原因（至少5个字符）..."
                            maxlength="200"
                            style="border: 2px solid #e9ecef; border-radius: 8px; resize: none;"></textarea>
                    <div class="d-flex justify-content-between mt-2">
                        <small class="text-muted">
                            <i class="fas fa-info-circle"></i> 请认真填写撤销原因
                        </small>
                        <small class="text-muted" id="cancelCharCount">0/200</small>
                    </div>
                </div>
            </div>
            <div class="modal-footer" style="border-top: 2px solid #f0f0f0;">
                <button type="button" class="btn btn-secondary" data-dismiss="modal" style="border-radius: 8px; padding: 10px 20px;">
                    <i class="fas fa-times"></i> 取消
                </button>
                <button type="button" class="btn btn-warning" onclick="confirmCancel()" style="border-radius: 8px; padding: 10px 20px; font-weight: 600;">
                    <i class="fas fa-check"></i> 确认撤销
                </button>
            </div>
        </div>
    </div>
</div>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/4.6.2/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.bootcdn.net/ajax/libs/layer/3.5.1/layer.js"></script>

<script>
    var currentPage = 1;
    var pageSize = 10;
    var ctx = '${pageContext.request.contextPath}';
    var isLoading = false;
    var complaintData = [];

    $(function() {
        console.log('🚀 页面初始化...');
        loadComplaints();
        initCharCount();
        initBackToTop();

        // 表单回车提交
        $('#complaintForm').on('keypress', function(e) {
            if (e.which === 13 && e.target.tagName !== 'TEXTAREA') {
                e.preventDefault();
                submitComplaint();
            }
        });

        // 🔥 撤销原因字符计数
        $('#cancelReason').on('input', function() {
            var length = $(this).val().length;
            $('#cancelCharCount').text(length + '/200');

            if (length >= 180) {
                $('#cancelCharCount').addClass('text-danger font-weight-bold');
            } else {
                $('#cancelCharCount').removeClass('text-danger font-weight-bold');
            }
        });

        // 🔥 撤销弹窗显示时自动聚焦
        $('#cancelModal').on('shown.bs.modal', function() {
            $('#cancelReason').focus();
        });

        // 🔥 追加说明弹窗显示时自动聚焦
        $('#appendModal').on('shown.bs.modal', function() {
            $('#appendContent').focus();
        });
    });

    function initCharCount() {
        $('#title').on('input', function() {
            var length = $(this).val().length;
            $('#titleCount').text(length + '/100').toggleClass('danger', length >= 90);
        });
        $('#content').on('input', function() {
            var length = $(this).val().length;
            $('#contentCount').text(length + '/500').toggleClass('danger', length >= 450);
        });
    }

    function initBackToTop() {
        $(window).scroll(function() {
            if ($(this).scrollTop() > 300) $('#backToTop').fadeIn();
            else $('#backToTop').fadeOut();
        });
    }

    function scrollToTop() {
        $('html, body').animate({scrollTop: 0}, 600);
    }

    function resetSearch() {
        $('#searchKeyword').val('');
        $('#filterType').val('');
        $('#filterStatus').val('');
        loadComplaints(1);
    }

    function submitComplaint() {
        var form = $('#complaintForm')[0];
        if (!form.checkValidity()) {
            form.reportValidity();
            return;
        }

        var data = {
            complaintType: $('#complaintType').val(),
            title: $('#title').val().trim(),
            content: $('#content').val().trim(),
            isAnonymous: $('#isAnonymous').is(':checked') ? 1 : 0
        };

        if (!data.complaintType || !data.title || !data.content) {
            layer.msg('请填写完整信息', {icon: 2});
            return;
        }

        var submitBtn = $('button[onclick="submitComplaint()"]');
        submitBtn.prop('disabled', true).html('<i class="fas fa-spinner fa-spin"></i> 提交中...');

        $.ajax({
            url: ctx + '/owner/complaint/submit',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(data),
            dataType: 'json',
            success: function(res) {
                if (res.success) {
                    layer.msg('提交成功', {icon: 1, time: 1500});
                    $('#complaintForm')[0].reset();
                    $('#titleCount').text('0/100');
                    $('#contentCount').text('0/500');
                    loadComplaints(1);
                } else {
                    layer.msg(res.message || '提交失败', {icon: 2});
                }
            },
            error: function() { layer.msg('网络错误', {icon: 2}); },
            complete: function() {
                submitBtn.prop('disabled', false).html('<i class="fas fa-paper-plane"></i> 提交反馈');
            }
        });
    }

    /**
     * 🔥 打开撤销弹窗
     */
    function cancelComplaint(id) {
        console.log('🔍 打开撤销弹窗，ID:', id);

        // 设置投诉ID
        $('#cancelComplaintId').val(id);

        // 清空输入框
        $('#cancelReason').val('');
        $('#cancelCharCount').text('0/200').removeClass('text-danger font-weight-bold');

        // 隐藏详情弹窗
        $('#detailModal').modal('hide');

        // 延迟显示撤销弹窗（等待详情弹窗完全关闭）
        setTimeout(function() {
            $('#cancelModal').modal('show');
        }, 300);
    }

    /**
     * 🔥 确认撤销
     */
    function confirmCancel() {
        var id = $('#cancelComplaintId').val();
        var reason = $('#cancelReason').val().trim();

        // 验证投诉ID
        if (!id) {
            layer.msg('投诉ID缺失', {icon: 2});
            return;
        }

        // 验证撤销原因
        if (!reason) {
            layer.msg('请输入撤销原因', {icon: 0});
            $('#cancelReason').focus();
            return;
        }

        if (reason.length < 5) {
            layer.msg('撤销原因至少需要5个字符', {icon: 0});
            $('#cancelReason').focus();
            return;
        }

        // 禁用按钮，防止重复提交
        var submitBtn = $('button[onclick="confirmCancel()"]');
        submitBtn.prop('disabled', true).html('<i class="fas fa-spinner fa-spin"></i> 提交中...');

        // 发送请求
        $.ajax({
            url: ctx + '/owner/complaint/cancel',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                complaintId: parseInt(id),
                reason: reason
            }),
            dataType: 'json',
            success: function(res) {
                if (res.success) {
                    layer.msg('撤销成功', {icon: 1, time: 1500});
                    $('#cancelModal').modal('hide');
                    loadComplaints(currentPage);
                } else {
                    layer.msg(res.message || '撤销失败', {icon: 2});
                }
            },
            error: function(xhr, status, error) {
                console.error('❌ 撤销失败:', error);
                console.error('响应状态:', xhr.status);
                console.error('响应内容:', xhr.responseText);
                layer.msg('网络错误，请稍后重试', {icon: 2});
            },
            complete: function() {
                submitBtn.prop('disabled', false).html('<i class="fas fa-check"></i> 确认撤销');
            }
        });
    }

    function deleteComplaint(id) {
        layer.confirm('确定要删除这条投诉记录吗？删除后无法恢复。', {
            icon: 3,
            title: '确认删除',
            btn: ['确定', '取消']
        }, function(index) {
            layer.close(index);

            var loadIdx = layer.load(1);
            $.ajax({
                url: ctx + '/owner/complaint/delete',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({ complaintId: id }),
                success: function(res) {
                    layer.close(loadIdx);
                    if (res.success) {
                        layer.msg('删除成功', {icon: 1});
                        $('#detailModal').modal('hide');
                        loadComplaints(currentPage);
                    } else {
                        layer.msg(res.message || '删除失败', {icon: 2});
                    }
                },
                error: function() {
                    layer.close(loadIdx);
                    layer.msg('网络错误', {icon: 2});
                }
            });
        });
    }

    function openAppendModal(id) {
        console.log('🔍 打开追加说明弹窗，ID:', id);
        $('#appendComplaintId').val(id);
        $('#appendContent').val('');
        $('#detailModal').modal('hide');
        setTimeout(function() {
            $('#appendModal').modal('show');
        }, 300);
    }

    function submitAppend() {
        var id = $('#appendComplaintId').val();
        var content = $('#appendContent').val().trim();

        if (!id) {
            layer.msg('投诉ID缺失', {icon: 2});
            return;
        }

        if (!content) {
            layer.msg('请输入补充内容', {icon: 2});
            return;
        }

        var complaintId = parseInt(id);
        if (isNaN(complaintId)) {
            layer.msg('投诉ID格式错误', {icon: 2});
            return;
        }

        var requestData = {
            complaintId: complaintId,
            content: content
        };

        var submitBtn = $('button[onclick="submitAppend()"]');
        submitBtn.prop('disabled', true).html('<i class="fas fa-spinner fa-spin"></i> 提交中...');

        $.ajax({
            url: ctx + '/owner/complaint/append',
            type: 'POST',
            contentType: 'application/json; charset=UTF-8',
            data: JSON.stringify(requestData),
            dataType: 'json',
            success: function(res) {
                if (res.success) {
                    layer.msg('追加成功', {icon: 1});
                    $('#appendModal').modal('hide');
                    loadComplaints(currentPage);
                } else {
                    layer.msg(res.message || '操作失败', {icon: 2});
                }
            },
            error: function(xhr, status, error) {
                console.error('❌ 追加说明失败:', error);
                layer.msg('网络错误', {icon: 2});
            },
            complete: function() {
                submitBtn.prop('disabled', false).html('<i class="fas fa-check"></i> 确认提交');
            }
        });
    }

    function showDetail(id) {
        var item = complaintData.find(function(c) {
            return (c.complaintId || c.complaint_id) == id;
        });

        if (!item) {
            layer.msg('数据加载失败', {icon: 2});
            return;
        }

        var status = item.complaintStatus || item.complaint_status;
        var type = item.complaintType || item.complaint_type;
        var reply = item.reply;

        // 🔥 判断是否撤销及类型
        var isOwnerCancelled = (status === 'closed' && reply && reply.indexOf('【业主主动撤销】') > -1);
        var isAdminRejected = (status === 'closed' && reply && reply.indexOf('【管理员驳回】') > -1);
        var isCancelled = isOwnerCancelled || isAdminRejected;

        var displayStatus = isCancelled ? 'cancelled' : status;
        var statusName = isCancelled ? '已撤销' : getStatusName(status);

        var html = '';

        // 头部信息
        html += '<div class="detail-header-info">' +
            '<h4 class="detail-title">' + escapeHtml(item.title) + '</h4>' +
            '<div class="detail-badges">' +
            '<span class="type-badge type-' + type + '">' +
            '<i class="fas fa-tag"></i>' + getTypeName(type) +
            '</span>' +
            '<span class="status-badge status-' + displayStatus + '">' + statusName + '</span>' +
            '</div>' +
            '</div>';

        // 原始内容
        var fullContent = item.content || '';
        fullContent = fullContent.replace(/\r\n/g, '\n').replace(/\r/g, '\n');
        var firstAppendIndex = fullContent.indexOf('【');
        var originalContent = firstAppendIndex === -1 ? fullContent.trim() : fullContent.substring(0, firstAppendIndex).trim();

        html += '<div class="content-section">' +
            '<div class="section-title"><i class="fas fa-file-text"></i>原始内容</div>' +
            '<div class="content-text">' + escapeHtml(originalContent) + '</div>' +
            '</div>';

        // 追加说明
        var appendMatches = [];
        var appendRegex = /【(\d{4}-\d{2}-\d{2}\s+\d{2}:\d{2}:\d{2})\s+追加】/g;
        var matches = [];
        var match;

        while ((match = appendRegex.exec(fullContent)) !== null) {
            matches.push({
                time: match[1],
                index: match.index,
                fullMatch: match[0]
            });
        }

        for (var i = 0; i < matches.length; i++) {
            var currentMatch = matches[i];
            var startIndex = currentMatch.index + currentMatch.fullMatch.length;
            var endIndex = i < matches.length - 1 ? matches[i + 1].index : fullContent.length;
            var appendContent = fullContent.substring(startIndex, endIndex).trim();

            appendMatches.push({
                time: currentMatch.time,
                content: appendContent
            });
        }

        if (appendMatches.length > 0) {
            html += '<div class="append-timeline">' +
                '<div class="timeline-header">' +
                '<div class="timeline-title"><i class="fas fa-stream"></i>追加说明记录</div>' +
                '<span class="timeline-count">' + appendMatches.length + ' 条</span>' +
                '</div>';

            appendMatches.forEach(function(append) {
                html += '<div class="timeline-item">' +
                    '<div class="timeline-dot"><i class="fas fa-plus"></i></div>' +
                    '<div class="timeline-content">' +
                    '<div class="timeline-time"><i class="far fa-clock"></i>' + append.time + '</div>' +
                    '<div class="timeline-text">' + escapeHtml(append.content) + '</div>' +
                    '</div></div>';
            });

            html += '</div>';
        }

        // 元信息
        html += '<div class="meta-info">' +
            '<div class="meta-item">' +
            '<div class="meta-label"><i class="far fa-clock"></i>提交时间</div>' +
            '<div class="meta-value">' + formatTime(item.submitTime) + '</div>' +
            '</div>';

        if (!item.isAnonymous) {
            html += '<div class="meta-item">' +
                '<div class="meta-label"><i class="fas fa-user"></i>提交人</div>' +
                '<div class="meta-value">' + escapeHtml(item.ownerName) + ' · ' + item.ownerPhone + '</div>' +
                '</div>';
        }

        html += '</div>';

        // 🔥 物业回复/撤销说明（去掉前缀）
        if (reply) {
            var replyClass = 'reply-section';
            var displayReply = reply;
            var replyTitle = '物业回复';
            var replyIcon = 'fa-reply';

            if (isOwnerCancelled) {
                replyClass += ' owner-cancelled';
                replyTitle = '撤销说明';
                replyIcon = 'fa-undo';

                var ownerCancelMatch = reply.match(/【业主主动撤销】原因：(.+)/);
                if (ownerCancelMatch && ownerCancelMatch[1]) {
                    displayReply = ownerCancelMatch[1].trim();
                } else {
                    displayReply = reply.replace(/【业主主动撤销】原因：/g, '').trim();
                    if (!displayReply) {
                        displayReply = '业主已主动撤销该投诉';
                    }
                }
            } else if (isAdminRejected) {
                replyClass += ' rejected';
                replyTitle = '驳回说明';
                replyIcon = 'fa-ban';

                var adminRejectMatch = reply.match(/【管理员驳回】原因：(.+)/);
                if (adminRejectMatch && adminRejectMatch[1]) {
                    displayReply = adminRejectMatch[1].trim();
                } else {
                    displayReply = reply.replace(/【管理员驳回】原因：/g, '').replace(/【管理员驳回】/g, '').trim();
                    if (!displayReply) {
                        displayReply = '该投诉已被管理员驳回，如有疑问请联系物业';
                    }
                }
            }

            html += '<div class="' + replyClass + '">' +
                '<div class="reply-header">' +
                '<div class="reply-avatar"><i class="fas ' + replyIcon + '"></i></div>' +
                '<div class="reply-info">' +
                '<div class="reply-author">' + replyTitle + (item.handlerName ? ' (' + item.handlerName + ')' : '') + '</div>' +
                (item.replyTime ? '<div class="reply-time"><i class="far fa-clock"></i>' + formatTime(item.replyTime) + '</div>' : '') +
                '</div></div>' +
                '<div class="reply-content">' + escapeHtml(displayReply) + '</div>' +
                '</div>';
        }

        $('#detailContent').html(html);

        // 操作按钮
        var actionHtml = '<button type="button" class="btn btn-secondary" data-dismiss="modal" style="border-radius: 8px; padding: 8px 16px; font-size: 13px; margin-right: 8px;">' +
            '<i class="fas fa-times"></i> 关闭</button>';

        if (status === 'pending') {
            actionHtml += '<button type="button" class="btn btn-warning" onclick="cancelComplaint(' + id + ')" style="border-radius: 8px; padding: 8px 16px; font-size: 13px; margin-right: 8px;">' +
                '<i class="fas fa-undo"></i> 撤销</button>';
            actionHtml += '<button type="button" class="btn btn-info" onclick="openAppendModal(' + id + ')" style="border-radius: 8px; padding: 8px 16px; font-size: 13px;">' +
                '<i class="fas fa-plus"></i> 追加说明</button>';
        }

        if (status === 'processing') {
            actionHtml += '<button type="button" class="btn btn-info" onclick="openAppendModal(' + id + ')" style="border-radius: 8px; padding: 8px 16px; font-size: 13px;">' +
                '<i class="fas fa-plus"></i> 追加说明</button>';
        }

        if (status === 'closed' || status === 'resolved' || isCancelled) {
            actionHtml += '<button type="button" class="btn btn-danger" onclick="deleteComplaint(' + id + ')" style="border-radius: 8px; padding: 8px 16px; font-size: 13px;">' +
                '<i class="fas fa-trash-alt"></i> 删除记录</button>';
        }

        $('#detailActions').html(actionHtml);
        $('#detailModal').modal('show');
    }

    function loadComplaints(page) {
        if (isLoading) return;
        if (page) currentPage = page;
        isLoading = true;

        var listDiv = $('#complaintList');
        listDiv.html('<div class="text-center py-5"><i class="fas fa-spinner fa-spin fa-2x text-muted"></i><p class="text-muted mt-3">加载中...</p></div>');

        $.ajax({
            url: ctx + '/owner/complaint/',
            type: 'GET',
            data: {
                keyword: $('#searchKeyword').val().trim(),
                complaintType: $('#filterType').val(),
                complaintStatus: $('#filterStatus').val(),
                pageNum: currentPage,
                pageSize: pageSize
            },
            success: function(res) {
                if (res.success) {
                    var list = res.data.list || res.data;
                    var total = res.data.total || 0;
                    var pages = res.data.pages || Math.ceil(total / pageSize);

                    complaintData = list;
                    renderComplaintList(list);
                    renderPagination(pages, total);
                } else {
                    showError(res.message);
                }
            },
            error: function() {
                showError('加载失败，请稍后重试');
            },
            complete: function() {
                isLoading = false;
            }
        });
    }

    function showError(msg) {
        $('#complaintList').html('<div class="empty-state"><i class="fas fa-exclamation-triangle"></i><p>' + msg + '</p></div>');
    }

    function renderComplaintList(list) {
        if (!list || list.length === 0) {
            $('#complaintList').html('<div class="empty-state"><i class="fas fa-inbox"></i><p>暂无记录</p></div>');
            return;
        }

        var html = '';
        list.forEach(function(item) {
            var status = item.complaintStatus || item.complaint_status;
            var type = item.complaintType || item.complaint_type;
            var id = item.complaintId || item.complaint_id;
            var reply = item.reply;

            var isCancelled = (status === 'closed' && reply &&
                (reply.indexOf('【业主主动撤销】') > -1 || reply.indexOf('【管理员驳回】') > -1));

            var cardClass = isCancelled ? 'complaint-card cancelled' : 'complaint-card';
            var displayStatus = isCancelled ? 'cancelled' : status;
            var statusName = isCancelled ? '已撤销' : getStatusName(status);

            var firstAppendIndex = item.content.indexOf('【');
            var originalContent = firstAppendIndex === -1 ? item.content : item.content.substring(0, firstAppendIndex);
            originalContent = originalContent.trim();
            var contentPreview = originalContent.length > 80 ? originalContent.substring(0, 80) + '...' : originalContent;

            var appendCount = (item.content.match(/【\d{4}-\d{2}-\d{2}\s+\d{2}:\d{2}:\d{2}\s+追加】/g) || []).length;

            html += '<div class="' + cardClass + '" onclick="showDetail(' + id + ')">' +
                '<div class="complaint-header">' +
                '<div class="complaint-title">' + escapeHtml(item.title) + '</div>' +
                '<span class="status-badge status-' + displayStatus + '">' + statusName + '</span>' +
                '</div>' +
                '<div class="complaint-preview">' + escapeHtml(contentPreview) + '</div>' +
                '<div class="complaint-footer">' +
                '<div class="complaint-meta">' +
                '<span class="type-badge type-' + type + '"><i class="fas fa-tag"></i>' + getTypeName(type) + '</span>' +
                '<span><i class="far fa-clock"></i> ' + formatTime(item.submitTime) + '</span>' +
                (appendCount > 0 ? '<span><i class="fas fa-plus-circle"></i> 追加 ' + appendCount + ' 次</span>' : '') +
                (reply ? '<span><i class="fas fa-reply"></i> 已回复</span>' : '') +
                '</div>' +
                '<span class="text-primary" style="font-size: 12px; font-weight: 600;"><i class="fas fa-eye"></i> 点击查看详情</span>' +
                '</div></div>';
        });
        $('#complaintList').html(html);
    }

    function renderPagination(pages, total) {
        var paginationDiv = $('#pagination');

        if (total === 0) {
            paginationDiv.html('<div class="text-center mt-3"><small class="text-muted" style="font-size: 14px;"><i class="fas fa-inbox"></i> 暂无数据</small></div>');
            return;
        }

        var html = '<ul class="pagination pagination-sm justify-content-center mb-2">';

        html += '<li class="page-item ' + (currentPage == 1 ? 'disabled' : '') + '">' +
            '<a class="page-link" href="#" onclick="loadComplaints(' + (currentPage - 1) + '); return false;">' +
            '<i class="fas fa-chevron-left"></i> 上一页</a></li>';

        html += '<li class="page-item disabled"><span class="page-link">' +
            '<i class="fas fa-file-alt"></i> 第 ' + currentPage + ' / ' + pages + ' 页</span></li>';

        html += '<li class="page-item ' + (currentPage >= pages ? 'disabled' : '') + '">' +
            '<a class="page-link" href="#" onclick="loadComplaints(' + (currentPage + 1) + '); return false;">' +
            '下一页 <i class="fas fa-chevron-right"></i></a></li>';

        html += '</ul>';

        html += '<div class="text-center"><small class="text-muted" style="font-size: 14px;">' +
            '<i class="fas fa-list"></i> 共 <span class="text-primary font-weight-bold">' + total + '</span> 条记录</small></div>';

        paginationDiv.html(html);
    }

    function getTypeName(t) {
        return {'service':'保安/服务','environment':'卫生/环境','facility':'设施设备','fee':'费用疑问','other':'其他建议'}[t] || t;
    }

    function getStatusName(s) {
        return {
            'pending':'待处理',
            'processing':'处理中',
            'resolved':'已解决',
            'closed':'已关闭',
            'cancelled':'已撤销'
        }[s] || s;
    }

    function formatTime(ts) {
        if (!ts) return '';
        return ts.replace('T', ' ').substring(0, 16);
    }

    function escapeHtml(t) {
        if (!t) return '';
        return t.replace(/[&<>"']/g, function(m){return {'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#039;'}[m]});
    }
</script>

</body>
</html>
