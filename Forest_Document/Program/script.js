function toggleFolder(element) {
  var content = element.nextElementSibling;
  var icon = element.querySelector('.folder-icon');

  if (content.style.display === 'none') {
    content.style.display = 'block';
    icon.classList.remove('collapsed');
    icon.textContent = '📂';
  } else {
    content.style.display = 'none';
    icon.classList.add('collapsed');
    icon.textContent = '📁';
  }
}

// 全てのフォルダを開く関数
function expandAll() {
  var folders = document.querySelectorAll('.folder-content');
  folders.forEach(function (folder) {
    folder.style.display = 'block';
  });

  var icons = document.querySelectorAll('.folder-icon');
  icons.forEach(function (icon) {
    icon.classList.remove('collapsed');
    icon.textContent = '📂';
  });
}

// 全てのフォルダを閉じる関数
function collapseAll() {
  var folders = document.querySelectorAll('.folder-content');
  folders.forEach(function (folder) {
    folder.style.display = 'none';
  });

  var icons = document.querySelectorAll('.folder-icon');
  icons.forEach(function (icon) {
    icon.classList.add('collapsed');
    icon.textContent = '📁';
  });
}

// ページ読み込み時に全てのフォルダを開いた状態にする
window.onload = function () {
  expandAll();
}