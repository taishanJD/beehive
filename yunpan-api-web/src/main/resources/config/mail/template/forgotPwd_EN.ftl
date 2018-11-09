<!DOCTYPE html>
<html lang="en" charset="UTF-8">
<head>
    <meta charset="UTF-8">
    <title>Reset Password</title>
    <style>
      .quarkdata { border: 1px solid #e8e8e8; width: 70%; margin-left: 15%; margin-top: 20px; color: #565656;}
      .top { background-color: #1D88DA; padding: 20px 50px;}
      .top img { display: inline-block; width: 80px; height: 60px; }
      .top h1 { color: white; display: inline; position: relative; left: 20px; top: -30px; }
      .content { padding: 30px 50px 60px 50px; word-break: break-all; }
      .foot { border-top: 1px solid #e8e8e8; padding: 20px 50px; }
      .foot img { display: inline-block; background-color: #0A5B9A; margin-right: 20px; width: 100px; height: 100px;}
      .foot span { position: relative; top: -40px;}
      .active { color: #0089e0; }
    </style>
</head>
<body>
  <div class="quarkdata">
    <div class="top">
      <img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADEAAAAxCAYAAABznEEcAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyZpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuNi1jMTM4IDc5LjE1OTgyNCwgMjAxNi8wOS8xNC0wMTowOTowMSAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENDIDIwMTcgKFdpbmRvd3MpIiB4bXBNTTpJbnN0YW5jZUlEPSJ4bXAuaWlkOkMzNEJGOTQwQkVERTExRTc4MTBCQjVFRENDNUQwQzJDIiB4bXBNTTpEb2N1bWVudElEPSJ4bXAuZGlkOkMzNEJGOTQxQkVERTExRTc4MTBCQjVFRENDNUQwQzJDIj4gPHhtcE1NOkRlcml2ZWRGcm9tIHN0UmVmOmluc3RhbmNlSUQ9InhtcC5paWQ6QzM0QkY5M0VCRURFMTFFNzgxMEJCNUVEQ0M1RDBDMkMiIHN0UmVmOmRvY3VtZW50SUQ9InhtcC5kaWQ6QzM0QkY5M0ZCRURFMTFFNzgxMEJCNUVEQ0M1RDBDMkMiLz4gPC9yZGY6RGVzY3JpcHRpb24+IDwvcmRmOlJERj4gPC94OnhtcG1ldGE+IDw/eHBhY2tldCBlbmQ9InIiPz63DBwrAAADaklEQVR42uyaS0gVURjH514zUizCXt5WpkEPe0FlBpUgPTb2wCAxM6KVG8sWUkQtqkVCWS1aWC0KklIIs4UhRYGbsgdWFAqBlSgY5i6DtK63/5f/G9P13pkz1zPZXPzgh9eZ8/rPfN+Zc74ZXygUMiJsKigGRSAPzAV+Y+JsBPSDNtAIGsDwXyVEhIl80BX6v62L4/wzbrOAUhAMecOCHO/vsfvoTvng8QS7TTxuVgBaRYTEQCfIMrxnH8ASP4PYiwIMjrvYz1nIy1Yk7tSHHxkeFvFZRAQ9FtBjAtzvsoBPoAp8dLEPv+HSPD4AjoEUzuXTwFEet7LvoB6UgGyQAarsOtMtohNUgLSIlUCYNJ7viCL6BEgHyaAQnARbWK/bqlNfKMriyYFJPL0GLaAJvHRQdzXYxTZqQBKoBBUgnWWkvVwwYDo2xpyI6AH32GA3eA/egG+afDsHrAGZYDbYyQtTB55Z1nTgKk0xXMQtGhgXN+0GNkXjLLGUt17VnoMOi/PnQBootWtIp4it4KKD8kdsRLyiG/n+pYiw3eZVjmVyt0oU2jnM4DcmQsQDcMPi/AFFEZvUn3YJYJMiJkVMiohfxCLNfffq6s+JiMUgoGHwsuArB3csygTYnyvutF2DCMngXeEiL5YVuhkTZRpE/AAXwD5d/TgVsQGssClzXRbHEbwFC4zRPG8d102xTNrf6Pbs5DTA74L14Ct4CPbalD/u9hR7jYNaqFj+LFhGXiish9YxmefInCwAxSUOgfPGaO42l/4dzb7Q52UxOMS/023aTwa1cUWZ4q5uCCwHZaZj1RE7sUoefwQCkuMFlyzarIyoXx1vdkJ1j30G3ALtIMV0XJbU9aadnSAvQiRjvYqo7Oz2cOo13LoTfSAVPI1ybhgUjHMvvZntxG0qgV3Dh1xelHODIHscz4zd4D7jwXDzTswBLVFi5DKYxbt0miQpXn0pd0pXxs5ORA/LNPP/bg52HgO3HPSayovLrbQRkAOe6Ew72okYBDMktQNmsvx85ll7YtT5CWpBVsTgM8FVntdqKrPTO9AMUsFaPpB8Cp4apL9L3W1gB1OV2i1h3k/0e3xj1y8i2jwuos3PJ6yXrTFh3mPLxx4Hud7xVEBz3MPhWakV7PeQkBGOtzW87PD8Vza+RPje6ZcAAwAYtnYpkQwVuQAAAABJRU5ErkJggg==">
      <h1>ThunderEMM</h1>
    </div>
    <div class="content">
      <p>Dear <span class="active" >${username}</span>:</p>
      <br>
      <p>Welcome to ThunderEMM system!</p>
      <p>You can use the following link to reset your password:</p>
      <p><a class="active" href="${url}">${url}</a></p>
      <p>If the above link is not clickable, you can also copy the link, paste it into your browser's address bar and press Enter to open the Reset Password page.</p>
      <br>
      <p>If you don’t use this link within ${validTime} hours, it will expire.</p>
      <p>This is an automatically generated message without reply.</p>
      <br>
      <p>To ensure the security of your account, please do not forward this email to anyone.</p>
      <p>If you have any questions, please contact the manager of your business. <span class="active">${admin}</span></p>
    </div>
    <div class="foot">
      <img src="${qrCodeAndroid}">
      <img src="${qrCodeIOS}">
      <span>Sweep, download ThunderEMM client</span>
    </div>
  </div>
</body>
</html>