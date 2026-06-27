$ErrorActionPreference = 'Stop'

$root = Split-Path -Parent $PSScriptRoot
$resources = Join-Path $root 'src\main\resources'
$assets = Join-Path $resources 'assets\pathfinding_beacon'
$data = Join-Path $resources 'data\pathfinding_beacon'

$directories = @(
    "$assets\textures\block",
    "$assets\textures\item",
    "$assets\blockstates",
    "$assets\models\block",
    "$assets\models\item",
    "$assets\lang",
    "$data\loot_tables\blocks",
    "$data\recipes"
)
$directories | ForEach-Object { New-Item -ItemType Directory -Path $_ -Force | Out-Null }

function Write-Utf8NoBom([string]$Path, [string]$Content) {
    [System.IO.File]::WriteAllText($Path, $Content, [System.Text.UTF8Encoding]::new($false))
}

function Write-Json([string]$Path, $Value) {
    Write-Utf8NoBom $Path ($Value | ConvertTo-Json -Depth 10)
}

function Convert-Unicode([string]$Value) {
    return [System.Text.RegularExpressions.Regex]::Unescape($Value)
}

$colors = @(
    'FFFFFF','E53935','1E88E5','43A047','FDD835','8E24AA','00ACC1','FB8C00','6D4C41','546E7A',
    'D81B60','3949AB','00897B','7CB342','F4511E','5E35B1','039BE5','C0CA33','FFB300','757575',
    'AD1457','283593','00695C','558B2F','EF6C00','4527A0','0277BD','9E9D24','FF8F00','3E2723'
)

Add-Type -AssemblyName System.Drawing
for ($number = 1; $number -le 30; $number++) {
    $hex = $colors[$number - 1]
    $red = [Convert]::ToInt32($hex.Substring(0, 2), 16)
    $green = [Convert]::ToInt32($hex.Substring(2, 2), 16)
    $blue = [Convert]::ToInt32($hex.Substring(4, 2), 16)
    $background = [System.Drawing.Color]::FromArgb(255, $red, $green, $blue)
    $luminance = 0.2126 * $red + 0.7152 * $green + 0.0722 * $blue
    $foreground = if ($luminance -gt 150) { [System.Drawing.Color]::Black } else { [System.Drawing.Color]::White }

    $bitmap = [System.Drawing.Bitmap]::new(32, 32)
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    $graphics.Clear($background)
    $graphics.TextRenderingHint = [System.Drawing.Text.TextRenderingHint]::SingleBitPerPixelGridFit
    $fontSize = if ($number -lt 10) { 23 } else { 18 }
    $font = [System.Drawing.Font]::new('Arial', $fontSize, [System.Drawing.FontStyle]::Bold, [System.Drawing.GraphicsUnit]::Pixel)
    $format = [System.Drawing.StringFormat]::new()
    $format.Alignment = [System.Drawing.StringAlignment]::Center
    $format.LineAlignment = [System.Drawing.StringAlignment]::Center
    $brush = [System.Drawing.SolidBrush]::new($foreground)
    $graphics.DrawString([string]$number, $font, $brush, [System.Drawing.RectangleF]::new(0, -1, 32, 34), $format)
    $bitmap.Save("$assets\textures\block\route_block_$number.png", [System.Drawing.Imaging.ImageFormat]::Png)
    $brush.Dispose(); $format.Dispose(); $font.Dispose(); $graphics.Dispose(); $bitmap.Dispose()

    Write-Json "$assets\blockstates\route_block_$number.json" @{
        variants = @{ '' = @{ model = "pathfinding_beacon:block/route_block_$number" } }
    }
    Write-Json "$assets\models\block\route_block_$number.json" @{
        parent = 'minecraft:block/cube_all'
        textures = @{ all = "pathfinding_beacon:block/route_block_$number" }
    }
    Write-Json "$assets\models\item\route_block_$number.json" @{
        parent = "pathfinding_beacon:block/route_block_$number"
    }
    Write-Json "$data\loot_tables\blocks\route_block_$number.json" @{
        type = 'minecraft:block'
        pools = @(@{
            rolls = 1
            entries = @(@{ type = 'minecraft:item'; name = "pathfinding_beacon:route_block_$number" })
            conditions = @(@{ condition = 'minecraft:survives_explosion' })
        })
    }

    if ($number -eq 1) {
        $ingredients = @(@{ item = 'minecraft:cobblestone' })
    } else {
        $ingredients = @(
            @{ item = "pathfinding_beacon:route_block_$($number - 1)" },
            @{ item = 'minecraft:cobblestone' }
        )
    }
    Write-Json "$data\recipes\route_block_$number.json" @{
        type = 'minecraft:crafting_shapeless'
        category = 'building'
        ingredients = $ingredients
        result = @{ item = "pathfinding_beacon:route_block_$number"; count = 64 }
    }
}
Copy-Item "$assets\textures\block\route_block_1.png" "$assets\icon.png" -Force

function New-ToolTexture([string]$Path, [string]$Kind) {
    $bitmap = [System.Drawing.Bitmap]::new(32, 32)
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    $graphics.Clear([System.Drawing.Color]::Transparent)
    $graphics.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::None
    $blue = [System.Drawing.SolidBrush]::new([System.Drawing.Color]::FromArgb(255, 0, 102, 255))
    if ($Kind -eq 'pickaxe') {
        $graphics.FillPolygon($blue, @(
            [System.Drawing.Point]::new(3, 5), [System.Drawing.Point]::new(27, 5),
            [System.Drawing.Point]::new(30, 9), [System.Drawing.Point]::new(18, 10),
            [System.Drawing.Point]::new(11, 29), [System.Drawing.Point]::new(6, 27),
            [System.Drawing.Point]::new(14, 10), [System.Drawing.Point]::new(5, 11)
        ))
    } else {
        $graphics.FillPolygon($blue, @(
            [System.Drawing.Point]::new(17, 3), [System.Drawing.Point]::new(29, 6),
            [System.Drawing.Point]::new(28, 16), [System.Drawing.Point]::new(20, 18),
            [System.Drawing.Point]::new(12, 29), [System.Drawing.Point]::new(7, 26),
            [System.Drawing.Point]::new(15, 15), [System.Drawing.Point]::new(12, 10)
        ))
    }
    $bitmap.Save($Path, [System.Drawing.Imaging.ImageFormat]::Png)
    $blue.Dispose(); $graphics.Dispose(); $bitmap.Dispose()
}

New-ToolTexture "$assets\textures\item\pathfinding_block_canceller.png" 'pickaxe'
New-ToolTexture "$assets\textures\item\id_sequence_reorderer.png" 'axe'
Write-Json "$assets\models\item\pathfinding_block_canceller.json" @{
    parent = 'minecraft:item/handheld'
    textures = @{ layer0 = 'pathfinding_beacon:item/pathfinding_block_canceller' }
}
Write-Json "$assets\models\item\id_sequence_reorderer.json" @{
    parent = 'minecraft:item/handheld'
    textures = @{ layer0 = 'pathfinding_beacon:item/id_sequence_reorderer' }
}

Write-Json "$data\recipes\pathfinding_block_canceller.json" @{
    type = 'minecraft:crafting_shaped'
    category = 'equipment'
    pattern = @('CCC', ' C ', ' C ')
    key = @{ C = @{ item = 'minecraft:cobblestone' } }
    result = @{ item = 'pathfinding_beacon:pathfinding_block_canceller'; count = 1 }
}
Write-Json "$data\recipes\id_sequence_reorderer.json" @{
    type = 'minecraft:crafting_shaped'
    category = 'equipment'
    pattern = @('CC ', 'CC ', ' C ')
    key = @{ C = @{ item = 'minecraft:cobblestone' } }
    result = @{ item = 'pathfinding_beacon:id_sequence_reorderer'; count = 1 }
}

$language = [ordered]@{
    'itemGroup.pathfinding_beacon.pathfinding' = (Convert-Unicode '\u5bfb\u8def')
    'item.pathfinding_beacon.pathfinding_block_canceller' = (Convert-Unicode '\u5bfb\u8def\u65b9\u5757\u53d6\u6d88\u5668')
    'item.pathfinding_beacon.id_sequence_reorderer' = (Convert-Unicode 'ID\u987a\u5e8f\u91cd\u6392\u5668')
}
for ($number = 1; $number -le 30; $number++) {
    $language["block.pathfinding_beacon.route_block_$number"] = "$number$(Convert-Unicode '\u53f7\u5bfb\u8def\u65b9\u5757')"
}
Write-Json "$assets\lang\zh_cn.json" $language
